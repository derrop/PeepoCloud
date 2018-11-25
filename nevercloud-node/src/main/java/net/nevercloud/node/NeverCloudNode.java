package net.nevercloud.node;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import com.google.common.base.Preconditions;
import io.netty.util.internal.PlatformDependent;
import jline.console.ConsoleReader;
import lombok.Getter;
import net.nevercloud.lib.NeverCloudAPI;
import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.config.yaml.YamlConfigurable;
import net.nevercloud.lib.network.auth.Auth;
import net.nevercloud.lib.network.auth.NetworkComponentType;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.PacketInfo;
import net.nevercloud.lib.network.packet.PacketManager;
import net.nevercloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.nevercloud.lib.node.NodeInfo;
import net.nevercloud.lib.server.BungeeCordProxyInfo;
import net.nevercloud.lib.server.BungeeGroup;
import net.nevercloud.lib.server.MinecraftGroup;
import net.nevercloud.lib.server.MinecraftServerInfo;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.lib.utility.network.NetworkAddress;
import net.nevercloud.node.addon.AddonManager;
import net.nevercloud.node.addon.defaults.DefaultAddonManager;
import net.nevercloud.node.addon.node.NodeAddon;
import net.nevercloud.node.command.CommandManager;
import net.nevercloud.node.command.CommandSender;
import net.nevercloud.node.command.defaults.*;
import net.nevercloud.node.database.DatabaseLoader;
import net.nevercloud.node.database.DatabaseManager;
import net.nevercloud.node.api.events.internal.EventManager;
import net.nevercloud.node.languagesystem.LanguagesManager;
import net.nevercloud.node.logging.ColoredLogger;
import net.nevercloud.node.logging.ConsoleColor;
import net.nevercloud.node.network.ClientNode;
import net.nevercloud.node.network.NetworkServer;
import net.nevercloud.node.network.packet.clientside.node.PacketCInUpdateNodeInfo;
import net.nevercloud.node.network.packet.serverside.server.PacketSOutCreateBungeeGroup;
import net.nevercloud.node.network.packet.serverside.server.PacketSOutCreateMinecraftGroup;
import net.nevercloud.node.network.packet.serverside.server.PacketSOutUpdateNodeInfo;
import net.nevercloud.node.network.participants.BungeeCordParticipant;
import net.nevercloud.node.network.participants.MinecraftServerParticipant;
import net.nevercloud.node.network.participants.NodeParticipant;
import net.nevercloud.node.server.ServerFilesLoader;
import net.nevercloud.node.server.processes.ServerQueue;
import net.nevercloud.node.statistics.StatisticsManager;
import net.nevercloud.node.updater.AutoUpdaterManager;

import java.io.IOException;
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
import java.util.concurrent.ScheduledExecutorService;

@Getter
public class NeverCloudNode implements NeverCloudAPI {

    @Getter
    private static NeverCloudNode instance;

    private ColoredLogger logger;
    private CommandManager commandManager;

    private SimpleJsonObject internalConfig;

    private DatabaseManager databaseManager;
    private DatabaseLoader databaseLoader;

    private LanguagesManager languagesManager;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);

    private AutoUpdaterManager autoUpdaterManager;

    private AddonManager<NodeAddon> nodeAddonManager;
    private DefaultAddonManager defaultAddonManager = new DefaultAddonManager();

    private String networkAuthKey;
    private NetworkServer networkServer;
    private Map<String, ClientNode> connectedNodes = new HashMap<>();

    private PacketManager networkServerPacketManager = new PacketManager();
    private PacketManager networkClientPacketManager = new PacketManager();

    private EventManager eventManager;

    private StatisticsManager statisticsManager = new StatisticsManager();

    private NodeInfo nodeInfo;

    private ServerQueue serverQueue;

    private CloudConfig cloudConfig;

    private Map<String, MinecraftGroup> minecraftGroups;
    private Map<String, BungeeGroup> bungeeGroups;

    private Map<String, MinecraftServerParticipant> serversOnThisNode = new HashMap<String, MinecraftServerParticipant>() {
        @Override
        public MinecraftServerParticipant put(String key, MinecraftServerParticipant value) {
            memoryUsedOnThisInstanceByServer += value.getServerInfo().getMemory();
            return super.put(key, value);
        }

        @Override
        public boolean remove(Object key, Object value) {
            if (value instanceof MinecraftServerParticipant) {
                memoryUsedOnThisInstanceByServer -= ((MinecraftServerParticipant) value).getServerInfo().getMemory();
            }
            return super.remove(key, value);
        }

        @Override
        public MinecraftServerParticipant remove(Object key) {
            MinecraftServerParticipant participant = this.get(key);
            if (participant != null) {
                memoryUsedOnThisInstanceByServer -= participant.getServerInfo().getMemory();
            }
            return super.remove(key);
        }

        @Override
        public void clear() {
            memoryUsedOnThisInstanceByServer = 0;
            super.clear();
        }
    };
    private Map<String, BungeeCordParticipant> proxiesOnThisNode = new HashMap<String, BungeeCordParticipant>() {
        @Override
        public BungeeCordParticipant put(String key, BungeeCordParticipant value) {
            memoryUsedOnThisInstanceByBungee += value.getProxyInfo().getMemory();
            return super.put(key, value);
        }

        @Override
        public boolean remove(Object key, Object value) {
            if (value instanceof BungeeCordParticipant) {
                memoryUsedOnThisInstanceByBungee -= ((BungeeCordParticipant) value).getProxyInfo().getMemory();
            }
            return super.remove(key, value);
        }

        @Override
        public BungeeCordParticipant remove(Object key) {
            BungeeCordParticipant participant = this.get(key);
            if (participant != null) {
                memoryUsedOnThisInstanceByBungee -= participant.getProxyInfo().getMemory();
            }
            return super.remove(key);
        }

        @Override
        public void clear() {
            memoryUsedOnThisInstanceByBungee = 0;
            super.clear();
        }
    };

    private int memoryUsedOnThisInstanceByBungee = 0;
    private int memoryUsedOnThisInstanceByServer = 0;

    private boolean running = true;

    NeverCloudNode() throws IOException {
        Preconditions.checkArgument(instance == null, "instance is already defined");
        instance = this;

        SystemUtils.setApi(this);

        ConsoleReader consoleReader = new ConsoleReader(System.in, System.out);
        this.logger = new ColoredLogger(consoleReader);

        this.nodeAddonManager = new AddonManager<>();

        this.loadConfigs();

        this.eventManager = new EventManager();

        this.internalConfig = SimpleJsonObject.load("internal/internalData.json");

        this.autoUpdaterManager = new AutoUpdaterManager();

        this.languagesManager = new LanguagesManager();

        this.databaseLoader = new DatabaseLoader("databaseAddons");
        this.databaseManager = this.databaseLoader.loadDatabaseManager(this);

        this.commandManager = new CommandManager(this.logger);

        this.installUpdates(this.commandManager.getConsole());

        this.executorService.execute(() -> {
            Thread thread = Thread.currentThread();
            while (!thread.isInterrupted()) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.sendPacketToNodes(new PacketSOutUpdateNodeInfo(this.nodeInfo));
            }
        });

        ServerFilesLoader.tryInstallSpigot();
        ServerFilesLoader.tryInstallBungee();

        this.serverQueue = ServerQueue.start();

        this.initCommands(this.commandManager);
        this.initPacketHandlers();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown0));

        this.reloadModules();
    }

    private void initPacketHandlers() {
        this.networkClientPacketManager.registerPacket(new PacketInfo(14, PacketCInUpdateNodeInfo.class, new PacketCInUpdateNodeInfo()));
    }

    private void initCommands(CommandManager commandManager) {
        commandManager.registerCommands(
                new CommandHelp(),
                new CommandStop(),
                new CommandAddon(),
                new CommandLanguage(),
                new CommandUpdate(),
                new CommandVersion(),
                new CommandReload(),
                new CommandClear()
        );
    }

    private void shutdown0() {
        running = false;
        this.commandManager.shutdown();
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

    private void loadConfigs() {
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

        Collection<NetworkAddress> oldNodes = this.cloudConfig == null ? null : this.cloudConfig.getConnectableNodes();
        if (this.cloudConfig == null) {
            this.cloudConfig = new CloudConfig();
        }
        this.cloudConfig.load();
        Collection<NetworkAddress> newNodes = this.cloudConfig.getConnectableNodes();

        if (this.networkServer == null) {
            this.networkServer = new NetworkServer(this.cloudConfig.getHost().getHost().equals("*") ? new InetSocketAddress(this.cloudConfig.getHost().getPort())
                    : new InetSocketAddress(this.cloudConfig.getHost().getHost(), this.cloudConfig.getHost().getPort()), this.networkServerPacketManager);
            this.networkServer.run();
        }

        if (oldNodes == null || !oldNodes.equals(newNodes)) {
            if (oldNodes == null) {
                oldNodes = newNodes;
                for (NetworkAddress node : oldNodes) {
                    this.connectToNode(node);
                }
            } else {
                for (NetworkAddress connectableNode : new ArrayList<>(oldNodes)) {
                    if (!newNodes.contains(connectableNode)) {
                        oldNodes.remove(connectableNode);
                        this.networkServer.closeNodeConnection(connectableNode.getHost());
                    }
                }
                for (NetworkAddress node : new ArrayList<>(newNodes)) {
                    if (!oldNodes.contains(node)) {
                        oldNodes.add(node);
                        this.connectToNode(node);
                    }
                }
            }
        }

    }

    public void tryConnectToNode(String host) {
        if (this.connectedNodes.containsKey(host))
            return;

        for (NetworkAddress node : this.cloudConfig.getConnectableNodes()) {
            if (node.getHost().equals(host)) {
                this.connectToNode(node);
                break;
            }
        }
    }

    private void connectToNode(NetworkAddress node) {
        ClientNode client = new ClientNode(new InetSocketAddress(node.getHost(), node.getPort()), this.networkClientPacketManager, new ChannelHandlerAdapter(),
                new Auth(this.networkAuthKey, this.cloudConfig.getNodeName(), NetworkComponentType.NODE, null, new SimpleJsonObject().append("nodeInfo", this.nodeInfo)),
                null);
        this.connectedNodes.put(node.getHost(), client);
        new Thread(client, "Node client @" + node.toString()).start();
    }

    public void installUpdates(CommandSender sender) {
        NeverCloudNode.getInstance().getAutoUpdaterManager().checkUpdates(updateCheckResponse -> {
            if (updateCheckResponse != null) {
                if (updateCheckResponse.isUpToDate()) {
                    sender.sendMessageLanguageKey("autoupdate.upToDate");
                } else {
                    sender.createLanguageMessage("autoupdate.versionsBehind").replace("%versionsBehind%", String.valueOf(updateCheckResponse.getVersionsBehind())
                            .replace("%newestVersion%", updateCheckResponse.getNewestVersion())).send();
                    NeverCloudNode.getInstance().getAutoUpdaterManager().update((success, path) -> {
                        if (success) {
                            sender.createLanguageMessage("autoupdate.successfullyUpdated").replace("%newestVersion%", updateCheckResponse.getNewestVersion()).send();
                            if (path != null) {
                                sender.createLanguageMessage("autoupdate.onWindows").replace("%path%", path.toString()).replace("%target%", SystemUtils.getPathOfInternalJarFile()).send();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            NeverCloudNode.getInstance().shutdown();
                        } else {
                            sender.createLanguageMessage("autoupdate.couldNotUpdate").replace("%newestVersion%", updateCheckResponse.getNewestVersion()).send();
                        }
                    });
                }
            } else {
                sender.sendMessageLanguageKey("autoupdate.error");
            }
        });
    }

    public void shutdown() {
        shutdown0();
        System.exit(0);
    }

    public void reload() {
        this.reloadModules();
        this.reloadConfigs();
    }

    public void reloadConfigs() {
        this.loadConfigs();
    }

    public void reloadModules() {
        this.nodeAddonManager.disableAndUnloadAddons();
        this.commandManager.getCommands().clear();
        this.eventManager.unregisterAll();
        this.initCommands(this.commandManager);
        this.eventManager.registerListener(this.statisticsManager);
        this.statisticsManager.clearListeners();
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

    public int getMemoryUsedOnThisInstance() {
        return this.memoryUsedOnThisInstanceByBungee + this.memoryUsedOnThisInstanceByServer + this.serverQueue.getMemoryNeededForProcessesInQueue();
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


    public ClientNode getConnectedNode(String name) {
        return this.connectedNodes.get(name);
    }


    @Override
    public BungeeGroup getBungeeGroup(String name) {
        return this.bungeeGroups.get(name);
    }

    @Override
    public MinecraftGroup getMinecraftGroup(String name) {
        return this.minecraftGroups.get(name);
    }

    public void sendPacketToNodes(Packet packet) {
        this.connectedNodes.values().forEach(networkClient -> networkClient.sendPacket(packet));
    }

    public void updateMinecraftGroup(MinecraftGroup group) {
        this.minecraftGroups.put(group.getName(), group);
        this.sendPacketToNodes(new PacketSOutCreateMinecraftGroup(group));
    }

    public void updateBungeeGroup(BungeeGroup group) {
        this.bungeeGroups.put(group.getName(), group);
        this.sendPacketToNodes(new PacketSOutCreateBungeeGroup(group));
    }

    public NodeInfo getBestNodeInfo(int memoryNeeded) {
        NodeInfo best = null;
        Collection<NodeInfo> infos = new ArrayList<>();
        this.connectedNodes.values().forEach(clientNode -> {
            if (clientNode.getNodeInfo() != null) {
                infos.add(clientNode.getNodeInfo());
            }
        });
        infos.add(this.nodeInfo);
        for (NodeInfo value : infos) {
            if (value != null && value.getMaxMemory() - value.getUsedMemory() >= memoryNeeded) {
                if (best == null) {
                    best = value;
                } else {
                    if (value.getUsedMemory() < best.getUsedMemory()) {
                        best = value;
                    }
                }
            }
        }
        return best;
    }

    public Collection<MinecraftServerParticipant> getMinecraftServers() {
        Collection<MinecraftServerParticipant> participants = new ArrayList<>(this.serversOnThisNode.values());
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            participants.addAll(value.getServers().values());
        }
        return participants;
    }

    public Collection<MinecraftServerParticipant> getMinecraftServers(String group) {
        Collection<MinecraftServerParticipant> participants = new ArrayList<>();
        for (MinecraftServerParticipant value : this.serversOnThisNode.values()) {
            if (value.getServerInfo().getGroupName().equalsIgnoreCase(group)) {
                participants.add(value);
            }
        }
        for (NodeParticipant node : this.networkServer.getConnectedNodes().values()) {
            for (MinecraftServerParticipant value : node.getServers().values()) {
                if (value.getServerInfo().getGroupName().equalsIgnoreCase(group)) {
                    participants.add(value);
                }
            }
        }
        return participants;
    }

    public Collection<BungeeCordParticipant> getBungeeProxies() {
        Collection<BungeeCordParticipant> participants = new ArrayList<>(this.proxiesOnThisNode.values());
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            participants.addAll(value.getProxies().values());
        }
        return participants;
    }

    public Collection<BungeeCordParticipant> getBungeeProxies(String group) {
        Collection<BungeeCordParticipant> participants = new ArrayList<>();
        for (BungeeCordParticipant value : this.proxiesOnThisNode.values()) {
            if (value.getProxyInfo().getGroupName().equalsIgnoreCase(group)) {
                participants.add(value);
            }
        }
        for (NodeParticipant node : this.networkServer.getConnectedNodes().values()) {
            for (BungeeCordParticipant value : node.getProxies().values()) {
                if (value.getProxyInfo().getGroupName().equalsIgnoreCase(group)) {
                    participants.add(value);
                }
            }
        }
        return participants;
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group) {
        return this.startMinecraftServer(group, group.getMemory());
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, int memory) {
        return this.startMinecraftServer(group, group.getName() + "-" + (this.getMinecraftServers(group.getName()).size() + 1), memory);
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name) {
        return this.startMinecraftServer(group, group.getName() + "-" + (this.getMinecraftServers(group.getName()).size() + 1), group.getMemory());
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name, int memory) {
        return startMinecraftServer(this.getBestNodeInfo(memory), group, name, memory);
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group) {
        return this.startMinecraftServer(nodeInfo, group, group.getMemory());
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, int memory) {
        return this.startMinecraftServer(nodeInfo, group, group.getName() + "-" + (this.getMinecraftServers(group.getName()).size() + 1), memory);
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name) {
        return this.startMinecraftServer(nodeInfo, group, group.getName() + "-" + (this.getMinecraftServers(group.getName()).size() + 1), group.getMemory());
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int memory) {
        if (nodeInfo == null)
            return null;
        if (this.nodeInfo.getName().equals(nodeInfo.getName())) {

        } else {

        }
        return null;
    }

}
