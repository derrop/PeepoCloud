package net.nevercloud.node;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import com.google.common.base.Preconditions;
import jline.console.ConsoleReader;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.nevercloud.lib.json.SimpleJsonObject;
import net.nevercloud.lib.network.NetworkClient;
import net.nevercloud.lib.network.auth.Auth;
import net.nevercloud.lib.network.auth.NetworkComponentType;
import net.nevercloud.lib.network.packet.PacketManager;
import net.nevercloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.nevercloud.lib.utility.NetworkAddress;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.addon.AddonManager;
import net.nevercloud.node.addon.defaults.DefaultAddonManager;
import net.nevercloud.node.addon.node.NodeAddon;
import net.nevercloud.node.command.CommandManager;
import net.nevercloud.node.command.defaults.*;
import net.nevercloud.node.database.DatabaseLoader;
import net.nevercloud.node.database.DatabaseManager;
import net.nevercloud.node.languagesystem.LanguagesManager;
import net.nevercloud.node.logging.ColoredLogger;
import net.nevercloud.node.logging.ConsoleColor;
import net.nevercloud.node.network.NetworkServer;
import net.nevercloud.node.updater.AutoUpdaterManager;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class NeverCloudNode {

    @Getter
    private static NeverCloudNode instance;

    private ColoredLogger logger;
    private CommandManager commandManager;

    private SimpleJsonObject internalConfig;

    private DatabaseManager databaseManager;
    private DatabaseLoader databaseLoader;

    private LanguagesManager languagesManager;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private AutoUpdaterManager autoUpdaterManager;

    private AddonManager<NodeAddon> nodeAddonManager;
    private DefaultAddonManager defaultAddonManager = new DefaultAddonManager();

    private String networkAuthKey;
    private NetworkServer networkServer;
    private Map<String, NetworkClient> connectedNodes = new HashMap<>();

    private PacketManager networkServerPacketManager = new PacketManager();
    private PacketManager networkClientPacketManager = new PacketManager();

    private Collection<NetworkAddress> connectableNodes;

    private String networkName;

    private boolean running = true;

    NeverCloudNode() throws IOException {
        Preconditions.checkArgument(instance == null, "instance is already defined");
        instance = this;

        ConsoleReader consoleReader = new ConsoleReader(System.in, System.out);
        this.logger = new ColoredLogger(consoleReader);

        this.loadAuthKey();
        this.loadNetworkConfig();

        this.internalConfig = SimpleJsonObject.load("internal/internalData.json");

        this.autoUpdaterManager = new AutoUpdaterManager();

        this.languagesManager = new LanguagesManager();

        this.databaseLoader = new DatabaseLoader("databaseAddons");
        this.databaseManager = this.databaseLoader.loadDatabaseManager(this);

        this.commandManager = new CommandManager(this.logger);

        this.initCommands(this.commandManager);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown0));

        this.nodeAddonManager = new AddonManager<>();
        this.reloadModules();
    }

    private void initCommands(CommandManager commandManager) {
        commandManager.registerCommands(
                new CommandHelp(),
                new CommandStop(),
                new CommandAddon(),
                new CommandLanguage(),
                new CommandUpdate(),
                new CommandVersion(),
                new CommandReload()
        );
    }

    private void shutdown0() {
        running = false;
        this.databaseLoader.shutdown();
        this.databaseManager.shutdown();

        this.nodeAddonManager.disableAndUnloadAddons();

        try {
            this.logger.getConsoleReader().print(ConsoleColor.RESET.toString());
            this.logger.getConsoleReader().drawLine();
            this.logger.getConsoleReader().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.logger.getConsoleReader().close();
    }

    private void loadAuthKey() {
        Path path = Paths.get("AUTH_KEY.node");
        if (Files.exists(path)) {
            try {
                this.networkAuthKey = new String(Files.readAllBytes(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.networkAuthKey = SystemUtils.randomString(2048);
            try {
                Files.write(path, this.networkAuthKey.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadNetworkConfig() {
        Path path = Paths.get("networking.yml");
        Configuration configuration = null;
        if (Files.exists(path)) {
            try (InputStream inputStream = Files.newInputStream(path)) {
                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            configuration = new Configuration();
            configuration.set("nodes", Arrays.asList(new NetworkAddress(getLocalAddress(), 0)));
            configuration.set("host", new NetworkAddress(getLocalAddress(), 2580));
            configuration.set("componentName", "Node-1");
            try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
                 Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (configuration == null) {
            System.err.println("&cThere was an error while loading the &enetworking.yml &cconfig");
            return;
        }

        this.networkName = configuration.getString("componentName");

        Collection<NetworkAddress> nodes = (Collection<NetworkAddress>) configuration.get("nodes");
        NetworkAddress host = (NetworkAddress) configuration.get("host");
        if (this.networkServer == null) {
            this.networkServer = new NetworkServer(this.networkServerPacketManager);
            this.networkServer.start(host.getHost().equals("*") ? new InetSocketAddress(host.getPort()) : new InetSocketAddress(host.getHost(), host.getPort()));
        }

        if (this.connectableNodes == null || !this.connectableNodes.equals(nodes)) {
            if (this.connectableNodes == null) {
                this.connectableNodes = nodes;
                for (NetworkAddress node : this.connectableNodes) {
                    this.connectToNode(node);
                }
            } else {
                for (NetworkAddress connectableNode : new ArrayList<>(this.connectableNodes)) {
                    if (!nodes.contains(connectableNode)) {
                        this.connectableNodes.remove(connectableNode);
                        this.networkServer.closeNodeConnection(connectableNode.getHost());
                    }
                }
                for (NetworkAddress node : new ArrayList<>(nodes)) {
                    if (!this.connectableNodes.contains(node)) {
                        this.connectableNodes.add(node);
                        this.connectToNode(node);
                    }
                }
            }
        }

    }

    public void tryConnectToNode(String host) {
        if (this.connectedNodes.containsKey(host))
            return;

        for (NetworkAddress node : this.connectableNodes) {
            if (node.getHost().equals(host)) {
                this.connectToNode(node);
                break;
            }
        }
    }

    private void connectToNode(NetworkAddress node) {
        NetworkClient client = new NetworkClient(node.getHost(), node.getPort(), this.networkClientPacketManager, new ChannelHandlerAdapter(),
                new Auth(this.networkAuthKey, this.networkName, NetworkComponentType.NODE, null));
        this.connectedNodes.put(node.getHost(), client);
        new Thread(client, "Node client @" + node.toString()).start();
    }

    public void shutdown() {
        shutdown0();
        System.exit(0);
    }

    public void reload() {
        this.reloadModules();
        this.reloadConfigs();

        this.loadAuthKey();
        this.loadNetworkConfig();
    }

    public void reloadConfigs() {

    }

    public void reloadModules() {
        this.nodeAddonManager.disableAndUnloadAddons();
        this.commandManager.getCommands().clear();
        this.initCommands(this.commandManager);
        try {
            this.nodeAddonManager.loadAddons("nodeAddons");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.nodeAddonManager.enableAddons();
    }

    public String getLocalAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            return "could not detect local address";
        }
    }

    public void saveInternalConfigFile() {
        this.internalConfig.saveAsFile("internal/internalData.json");
    }

    public String getMessage(String key) {
        return this.languagesManager.getMessage(key);
    }

    @Deprecated
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
}
