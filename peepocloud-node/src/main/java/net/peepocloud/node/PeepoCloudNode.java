package net.peepocloud.node;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import jline.console.ConsoleReader;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.in.PacketInToggleDebug;
import net.peepocloud.lib.network.packet.out.PacketOutToggleDebug;
import net.peepocloud.lib.network.packet.out.group.PacketOutCreateBungeeGroup;
import net.peepocloud.lib.network.packet.out.group.PacketOutCreateMinecraftGroup;
import net.peepocloud.lib.network.packet.out.server.PacketOutUpdateBungee;
import net.peepocloud.lib.network.packet.out.server.PacketOutUpdateServer;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.lib.scheduler.Scheduler;
import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.server.minecraft.MinecraftState;
import net.peepocloud.lib.users.UserManager;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.lib.utility.network.DirectQueryRequest;
import net.peepocloud.lib.utility.network.PacketSerializableWrapper;
import net.peepocloud.lib.utility.network.QueryRequest;
import net.peepocloud.node.addon.AddonManagerImpl;
import net.peepocloud.node.addon.defaults.DefaultAddonManagerImpl;
import net.peepocloud.node.api.PeepoCloudNodeAPI;
import net.peepocloud.node.api.addon.AddonManager;
import net.peepocloud.node.api.addon.defaults.DefaultAddonManager;
import net.peepocloud.node.api.addon.node.NodeAddon;
import net.peepocloud.node.api.command.CommandSender;
import net.peepocloud.node.api.database.DatabaseManager;
import net.peepocloud.node.api.event.DefaultEventManager;
import net.peepocloud.node.api.network.*;
import net.peepocloud.node.api.server.CloudProcess;
import net.peepocloud.node.api.server.TemplateStorage;
import net.peepocloud.node.api.statistic.StatisticsManager;
import net.peepocloud.node.command.CommandManagerImpl;
import net.peepocloud.node.command.defaults.*;
import net.peepocloud.node.database.DatabaseLoaderImpl;
import net.peepocloud.node.languagesystem.LanguagesManagerImpl;
import net.peepocloud.node.logging.ColoredLogger;
import net.peepocloud.node.network.ClientNodeImpl;
import net.peepocloud.node.network.ConnectableNode;
import net.peepocloud.node.network.NetworkManagerImpl;
import net.peepocloud.node.network.NetworkServer;
import net.peepocloud.node.network.packet.out.PacketOutUpdateNodeInfo;
import net.peepocloud.node.network.packet.out.group.PacketOutBungeeGroupDeleted;
import net.peepocloud.node.network.packet.out.group.PacketOutMinecraftGroupDeleted;
import net.peepocloud.node.network.packet.out.server.process.info.PacketOutQueryProcessInfo;
import net.peepocloud.node.network.participant.BungeeCordParticipantImpl;
import net.peepocloud.node.network.participant.MinecraftServerParticipantImpl;
import net.peepocloud.node.screen.ScreenManagerImpl;
import net.peepocloud.node.server.ServerFilesLoader;
import net.peepocloud.node.server.process.BungeeProcess;
import net.peepocloud.node.server.process.ProcessManager;
import net.peepocloud.node.server.process.ServerProcess;
import net.peepocloud.node.server.template.TemplateLocalStorage;
import net.peepocloud.node.setup.SetupImpl;
import net.peepocloud.node.updater.AutoUpdaterManager;
import net.peepocloud.node.updater.UpdateCheckResponse;
import net.peepocloud.node.utility.NodeUtils;
import net.peepocloud.node.utility.users.NodeUserManager;
import net.peepocloud.node.websocket.WebSocketClientImpl;
import org.reflections.Reflections;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class PeepoCloudNode extends PeepoCloudNodeAPI {

    @Getter
    private static PeepoCloudNode instance;

    private Scheduler scheduler;

    private ColoredLogger logger;
    private CommandManagerImpl commandManager;

    private SimpleJsonObject internalConfig;

    private DatabaseManager databaseManager;
    private DatabaseLoaderImpl databaseLoader;

    private LanguagesManagerImpl languagesManager;
    private NetworkManager networkManager = new NetworkManagerImpl(this);

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);

    private AutoUpdaterManager autoUpdaterManager;

    private AddonManager<NodeAddon> nodeAddonManager;
    private DefaultAddonManager defaultAddonManager = new DefaultAddonManagerImpl();

    private String networkAuthKey;
    private NetworkServer networkServer;
    private Map<String, ClientNodeImpl> connectedNodes = new HashMap<>();

    private PacketManager packetManager = new PacketManager();

    private ScreenManagerImpl screenManager = new ScreenManagerImpl();

    private DefaultEventManager eventManager;

    private StatisticsManager statisticsManager = new StatisticsManager();

    private NodeInfo nodeInfo;

    private CloudConfig cloudConfig;
    private GroupsConfig groupsConfig = new GroupsConfig();

    private Map<String, MinecraftGroup> minecraftGroups;
    private Map<String, BungeeGroup> bungeeGroups;

    private Map<UUID, PeepoPlayer> onlinePlayers = new ConcurrentHashMap<>();

    private ProcessManager processManager;

    private UserManager userManager = new NodeUserManager();

    private Collection<TemplateStorage> templateStorages = new ArrayList<>(Arrays.asList(new TemplateLocalStorage()));

    private SystemInfo systemInfo = new SystemInfo();

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

    private long startupTime;

    private boolean running = true;

    PeepoCloudNode() throws IOException {
        Preconditions.checkArgument(instance == null, "instance is already defined");
        instance = this;

        PeepoCloudNodeAPI.setInstance(this);

        //create factories for those classes
        try {
            Class.forName(WebSocketClientImpl.class.getName());
            Class.forName(SetupImpl.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        this.scheduler = new Scheduler();
        this.executorService.execute(this.scheduler);

        try {
            Field field = Charset.class.getDeclaredField("defaultCharset");
            field.setAccessible(true);
            field.set(null, StandardCharsets.UTF_8);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            System.err.println("Failed to set default charset");
        }

        ConsoleReader consoleReader = new ConsoleReader(System.in, System.out);
        this.logger = new ColoredLogger(consoleReader);

        {
            try {
                URLConnection connection = new URL(SystemUtils.CENTRAL_SERVER_URL + "banned").openConnection();
                connection.setConnectTimeout(2000);
                connection.connect();
                try (InputStream inputStream = connection.getInputStream();
                     Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    SimpleJsonObject jsonObject = new SimpleJsonObject(reader);
                    JsonObject response = jsonObject.get("response").getAsJsonObject();
                    if (response.get("banned").getAsBoolean()) {
                        String reason = response.has("reason") ? response.get("reason").getAsString() : null;
                        System.err.println("&4!!!");
                        System.err.println("&4!!!");
                        if (reason != null) {
                            System.err.println("&4YOUR CLOUD GOT BANNED, SOME FUNCTIONS WON'T WORK ANYMORE, REASON: &e" + reason);
                        } else {
                            System.err.println("&4YOUR CLOUD GOT BANNED, SOME FUNCTIONS WON'T WORK ANYMORE");
                        }
                        System.err.println("&4!!!");
                        System.err.println("&4!!!");
                        System.err.println("&4The Cloud will start in 5 seconds...");
                        SystemUtils.sleepUninterruptedly(5000);
                    }
                }
            } catch (Exception e) {
                if (!SystemUtils.isServerOffline(e)) {
                    e.printStackTrace();
                } else {
                    System.out.println("&cOur PeepoCloud UpdateServer (&e" + SystemUtils.CENTRAL_SERVER_URL + "&c) is currently offline, some functions won't work, we'll try to get it online as soon as possible");
                }
            }
        }

        this.internalConfig = SimpleJsonObject.load("internal/internalData.json");

        if (!this.internalConfig.contains("acceptedInformationsSendingToServer")) {
            System.out.println("&4Do you accept, that some data of your cloud and server (the ip address hashed, the ISP of your server, the name of your operating system, the version of the cloud, the uniqueId of your cloud, the maximum amount of memory of your cloud and the cpu cores) will be send to our server and saved there? We won't share the hashed ip address and the unique id of your cloud, but the other information are important for the support. Please type &eyes&4, if you agree, but please, you won't get any support for errors if you do not agree to this, because we need information about your system to help you.)");
            String s = this.logger.readLine1();
            if (s.equalsIgnoreCase("yes")) {
                System.out.println("&aYou have accepted that the data named above will be saved on our server.");
                this.internalConfig.append("acceptedInformationsSendingToServer", true);
                this.saveInternalConfigFile();
            } else {
                System.out.println("&cYou have not accepted that the data named above will be send to our server. You won't get any support if you have errors with the system.");
            }
        } else if (!this.internalConfig.getBoolean("acceptedInformationsSendingToServer")) {
            System.out.println("&cYou have not accepted that the data named above will be send to our server. You won't get any support if you have errors the system.");
        } else {
            System.out.println("&aYou have accepted that the ip address of your server hashed, the ISP of your server, the name of your operating system, the version of the cloud, the uniqueId of your cloud, the maximum amount of memory of your cloud and the cpu cores will be saved on our server.");
        }

        this.languagesManager = new LanguagesManagerImpl();

        this.commandManager = new CommandManagerImpl(this.logger);

        this.eventManager = new DefaultEventManager();
        this.eventManager.registerListener(this.statisticsManager);

        this.loadConfigs();

        this.databaseLoader = new DatabaseLoaderImpl("databaseAddons");
        this.databaseManager = this.databaseLoader.loadDatabaseManager();

        NodeUtils.updateNodeInfoForSupport(null);

        this.autoUpdaterManager = new AutoUpdaterManager();

        this.initCommands(this.commandManager);

        this.nodeAddonManager = new AddonManagerImpl<>();

        this.initPacketHandlers();

        if (this.cloudConfig.isAutoUpdate()) {
            this.installUpdatesSync(this.commandManager.getConsole());
        }

        try {
            this.nodeAddonManager.loadAddons("nodeAddons");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.nodeAddonManager.getLoadedAddons().values().forEach(nodeAddon -> nodeAddon.initPacketHandlers(this.packetManager));

        this.minecraftGroups = this.groupsConfig.loadMinecraftGroups();
        this.bungeeGroups = this.groupsConfig.loadBungeeGroups();

        this.processManager = new ProcessManager(
                bungee -> this.memoryUsedOnThisInstanceByBungee += bungee,
                server -> this.memoryUsedOnThisInstanceByServer += server,
                this.cloudConfig
        );

        this.scheduler.repeat(() -> {
            this.nodeInfo.setUsedMemory(this.getMemoryUsedOnThisInstance());
            this.nodeInfo.setCpuUsage(SystemUtils.cpuUsageProcess());
            this.networkManager.sendPacketToNodes(new PacketOutUpdateNodeInfo(this.nodeInfo));
        }, 10, 30, false);

        this.startupTime = System.currentTimeMillis();

        ServerFilesLoader.tryInstallSpigot();
        ServerFilesLoader.tryInstallBungee();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (this.isRunning()) {
                this.shutdown0();
            }
        }));

        this.nodeAddonManager.enableAddons();
    }

    private void initPacketHandlers() {
        this.packetManager.clearPacketHandlers();

        new Reflections("net.peepocloud.node.network.packet.in").getSubTypesOf(PacketHandler.class)
                .forEach(aClass -> {
                    try {
                        this.packetManager.registerPacket(aClass.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                    }
                });
        this.packetManager.registerPacket(new PacketInToggleDebug());

        this.nodeAddonManager.getLoadedAddons().values().forEach(nodeAddon -> nodeAddon.initPacketHandlers(this.packetManager));

        this.logger.debug("Registered " + this.packetManager.getRegisteredPackets().size() + " packet handlers");
    }


    private void initCommands(CommandManagerImpl commandManager) {
        commandManager.registerCommands(
                new CommandHelp(),
                new CommandStop(),
                new CommandAddon(),
                new CommandLanguage(),
                new CommandUpdate(),
                new CommandVersion(),
                new CommandReload(),
                new CommandClear(),
                new CommandCreate(),
                new CommandStart(),
                new CommandScreen(),
                new CommandUnique(),
                new CommandSupportUpdate(),
                new CommandStats(),
                new CommandGStats(),
                new CommandConfig(),
                new CommandList(),
                new CommandShutdown(),
                new CommandDelete(),
                new CommandDebug(),
                new CommandInfo()
        );

        this.logger.debug("Registered " + this.commandManager.getCommands().size() + " commands");
    }

    private void shutdown0() {
        running = false;
        if (this.processManager != null)
            this.processManager.shutdown();

        this.nodeAddonManager.disableAndUnloadAddons();

        this.connectedNodes.values().forEach(NetworkParticipant::close);
        this.getServerNodes().values().forEach(NodeParticipant::closeConnection);
        this.networkServer.close();

        this.commandManager.shutdown();
        this.databaseLoader.shutdown();
        this.databaseManager.shutdown();

        this.scheduler.disable();

        this.logger.shutdown();
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

        Collection<ConnectableNode> oldNodes = this.cloudConfig == null ? null : this.cloudConfig.getConnectableNodes();
        if (this.cloudConfig == null) {
            this.cloudConfig = new CloudConfig();
        }
        this.cloudConfig.load();
        Collection<ConnectableNode> newNodes = this.cloudConfig.getConnectableNodes();

        this.nodeInfo = this.cloudConfig.loadNodeInfo(this.processManager == null ? 0 : this.getMemoryUsedOnThisInstance());

        if (this.networkServer == null) {
            this.networkServer = new NetworkServer(this.cloudConfig.getHost().getHost().equals("*") ? new InetSocketAddress(this.cloudConfig.getHost().getPort())
                    : new InetSocketAddress(this.cloudConfig.getHost().getHost(), this.cloudConfig.getHost().getPort()), this.packetManager);
            this.networkServer.run();
        }

        if (oldNodes == null || !oldNodes.equals(newNodes)) {
            if (oldNodes == null) {
                oldNodes = newNodes;
                for (ConnectableNode node : oldNodes) {
                    if (node.getAddress().getHost() == null || node.getAddress().getHost().equalsIgnoreCase("host"))
                        continue;
                    this.connectToNode(node);
                }
            } else {
                for (ConnectableNode connectableNode : new ArrayList<>(oldNodes)) {
                    if (!newNodes.contains(connectableNode)) {
                        oldNodes.remove(connectableNode);
                        this.networkServer.closeNodeConnection(connectableNode.getAddress().getHost());
                    }
                }
                for (ConnectableNode node : new ArrayList<>(newNodes)) {
                    if (node.getAddress().getHost() == null || node.getAddress().getHost().equalsIgnoreCase("host"))
                        continue;
                    if (!oldNodes.contains(node)) {
                        oldNodes.add(node);
                        this.connectToNode(node);
                    }
                }
            }
        }

        this.statisticsManager.reload(this.cloudConfig.isUseGlobalStats());

    }

    public void tryConnectToNode(String host) {
        for (ClientNodeImpl value : this.connectedNodes.values()) {
            if (value.isConnected() && value.getAddress().equals(host))
                return;
        }

        for (ConnectableNode node : this.cloudConfig.getConnectableNodes()) {
            if (node.getAddress().getHost().equals(host)) {
                this.connectToNode(node);
                break;
            }
        }
    }

    public String getUniqueId() {
        return this.cloudConfig.getUniqueId();
    }

    public int getMaxMemory() {
        int maxMemory = this.cloudConfig.getMaxMemory();
        for (ClientNodeImpl value : this.connectedNodes.values()) {
            if (value.getNodeInfo() != null)
                maxMemory += value.getNodeInfo().getMaxMemory();
        }
        return maxMemory;
    }

    public int getMemoryUsed() {
        int used = this.getMemoryUsedOnThisInstance();
        for (ClientNodeImpl value : this.connectedNodes.values()) {
            if (value.getNodeInfo() != null)
                used += value.getNodeInfo().getUsedMemory();
        }
        return used;
    }

    private void connectToNode(ConnectableNode node) {
        SimpleJsonObject authData = new SimpleJsonObject().append("nodeInfo", this.nodeInfo);
        if (this.processManager != null && !this.processManager.getProcesses().isEmpty()) {
            authData.append("startingProxies", this.processManager.getProcesses().values().stream()
                    .filter(process -> process.isProxy() && !this.proxiesOnThisNode.containsKey(process.getName())).map(CloudProcess::getProxyInfo).collect(Collectors.toList()));
            authData.append("startingServers", this.processManager.getProcesses().values().stream()
                    .filter(process -> process.isServer() && process.getServerInfo().getState() == MinecraftState.OFFLINE)
                    .map(CloudProcess::getServerInfo).collect(Collectors.toList()));
        }
        if (!this.proxiesOnThisNode.isEmpty()) {
            authData.append("proxies", this.proxiesOnThisNode.values().stream().map(BungeeCordParticipant::getProxyInfo).collect(Collectors.toList()));
        }
        if (!this.serversOnThisNode.isEmpty()) {
            authData.append("servers", this.serversOnThisNode.values().stream().map(MinecraftServerParticipant::getServerInfo).collect(Collectors.toList()));
        }
        if (this.processManager != null && !this.processManager.getServerQueue().getServerProcesses().isEmpty()) {
            authData.append("queuedProxies", this.processManager.getServerQueue().getServerProcesses().stream()
                    .filter(CloudProcess::isProxy).map(CloudProcess::getProxyInfo).collect(Collectors.toList()));
            authData.append("queuedServers", this.processManager.getServerQueue().getServerProcesses().stream()
                    .filter(CloudProcess::isServer).map(CloudProcess::getServerInfo).collect(Collectors.toList()));
        }
        ClientNodeImpl client = new ClientNodeImpl(
                new InetSocketAddress(node.getAddress().getHost(), node.getAddress().getPort()),
                this.packetManager,
                new ChannelHandlerAdapter() {
                    @Override
                    public void disconnected(NetworkParticipant networkParticipant) {
                        connectedNodes.remove(node.getName());
                    }
                },
                new Auth(this.networkAuthKey, this.cloudConfig.getNodeName(), NetworkComponentType.NODE, null, authData),
                null);
        this.connectedNodes.put(node.getName(), client);
        new Thread(client, "Node client @" + node.toString()).start();
    }

    @Override
    public PeepoPlayer getPlayer(UUID uniqueId) {
        return this.onlinePlayers.get(uniqueId);
    }

    @Override
    public PeepoPlayer getPlayer(String name) {
        return this.onlinePlayers.values().stream().filter(peepoPlayer -> peepoPlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public TemplateStorage getTemplateStorage(String name) {
        for (TemplateStorage storage : this.templateStorages)
            if (storage.getName() != null && storage.getName().equals(name) && storage.isWorking())
                return storage;
        return null;
    }

    public boolean registerTemplateStorage(TemplateStorage storage) {
        for (TemplateStorage templateStorage : this.templateStorages) {
            if (templateStorage.getName() != null && templateStorage.getName().equals(storage.getName())) {
                return false;
            }
        }
        this.templateStorages.add(storage);
        return true;
    }

    public boolean unregisterTemplateStorage(TemplateStorage storage) {
        return this.templateStorages.remove(storage);
    }

    public boolean unregisterTemplateStorage(String name) {
        TemplateStorage a = null;
        for (TemplateStorage templateStorage : this.templateStorages) {
            if (templateStorage.getName().equals(name)) {
                a = templateStorage;
                break;
            }
        }
        if (a != null) {
            return this.unregisterTemplateStorage(a);
        }
        return false;
    }

    public void copyTemplate(MinecraftGroup group, Template template, Path target) {
        TemplateStorage storage = this.getTemplateStorage(template.getStorage());
        if (storage == null)
            storage = this.getTemplateStorage("local");
        storage.copyToPath(group, template, target);
    }

    public void copyTemplate(BungeeGroup group, Template template, Path target) {
        TemplateStorage storage = this.getTemplateStorage(template.getStorage());
        if (storage == null)
            storage = this.getTemplateStorage("local");
        storage.copyToPath(group, template, target);
    }

    /**
     * Checks asynchronous for updates and installs them
     * @param sender the sender to which the messages should be send
     */
    public void installUpdates(CommandSender sender) {
        PeepoCloudNode.getInstance().getAutoUpdaterManager().checkUpdates(updateCheckResponse -> this.installUpdates0(sender, updateCheckResponse));
    }

    /**
     * Checks synchronous for updates and installs them
     * @param sender the sender to which the messages should be send
     */
    public void installUpdatesSync(CommandSender sender) {
        this.installUpdates0(sender, PeepoCloudNode.getInstance().getAutoUpdaterManager().checkUpdatesSync());
    }

    private void installUpdates0(CommandSender sender, UpdateCheckResponse updateCheckResponse) {
        if (updateCheckResponse != null) {
            if (updateCheckResponse.isUpToDate()) {
                sender.sendMessageLanguageKey("autoupdate.upToDate");
            } else {
                sender.createLanguageMessage("autoupdate.versionsBehind").replace("%versionsBehind%", String.valueOf(updateCheckResponse.getVersionsBehind()))
                        .replace("%newestVersion%", updateCheckResponse.getNewestVersion()).send();
                PeepoCloudNode.getInstance().getAutoUpdaterManager().update((success, path) -> {
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
                        PeepoCloudNode.getInstance().shutdown();
                    } else {
                        sender.createLanguageMessage("autoupdate.couldNotUpdate").replace("%newestVersion%", updateCheckResponse.getNewestVersion()).send();
                    }
                });
            }
        } else {
            sender.sendMessageLanguageKey("autoupdate.error");
        }
    }

    public void shutdown() {
        shutdown0();
        System.exit(0);
    }

    public void reload() {
        this.reloadAddons();
        this.reloadConfigs();
    }

    public void reloadConfigs() {
        this.loadConfigs();
        this.nodeInfo = this.cloudConfig.loadNodeInfo(this.getMemoryUsedOnThisInstance());
    }

    public void reloadAddons() {
        this.nodeAddonManager.disableAndUnloadAddons();
        this.commandManager.unregisterAll();
        this.eventManager.unregisterAll();
        this.initCommands(this.commandManager);
        this.eventManager.registerListener(this.statisticsManager);
        this.statisticsManager.clearListeners();
        try {
            this.nodeAddonManager.loadAddons("nodeAddons");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.initPacketHandlers();
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
        return this.memoryUsedOnThisInstanceByBungee + this.memoryUsedOnThisInstanceByServer + this.processManager.getServerQueue().getMemoryNeededForProcessesInQueue();
    }

    public void saveInternalConfigFile() {
        this.internalConfig.saveAsFile("internal/internalData.json");
    }

    /**
     * Sets the {@link DatabaseManager} of this node instance
     * @param databaseManager the database manager
     * @deprecated should be only used for internal methods of the node
     */
    @Deprecated
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Map<String, NodeParticipant> getServerNodes() {
        return this.networkServer.getConnectedNodes();
    }

    public ClientNodeImpl getConnectedNode(String name) {
        return this.connectedNodes.get(name);
    }


    public BungeeGroup getBungeeGroup(String name) {
        return this.bungeeGroups.get(name);
    }

    @Override
    public void setDebuggingOnThisComponent(boolean enable) {
        this.logger.setDebugging(enable);
    }

    @Override
    public void debug(String message) {
        this.logger.debug(message);
    }

    //TODO

    @Override
    public void playerChat(UUID uniqueId, String message) {
    }

    @Override
    public void setPlayerTabHeaderFooter(UUID uniqueId, BaseComponent[] header, BaseComponent[] footer) {
    }

    @Override
    public void sendPlayer(UUID uniqueId, String server) {
    }

    @Override
    public void sendPlayerFallback(UUID uniqueId) {
    }

    @Override
    public void sendPlayerActionBar(UUID uniqueId, String message) {
    }

    @Override
    public void sendPlayerActionBar(UUID uniqueId, BaseComponent... message) {
    }

    @Override
    public void sendPlayerMessage(UUID uniqueId, String message) {
    }

    @Override
    public void sendPlayerMessage(UUID uniqueId, BaseComponent... components) {
    }

    @Override
    public void sendPlayerTitle(UUID uniqueId, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
    }

    @Override
    public void sendPlayerTitle(UUID uniqueId, BaseComponent[] title, BaseComponent[] subTitle, int fadeIn, int stay, int fadeOut) {
    }

    @Override
    public void kickPlayer(UUID uniqueId, String reason) {
    }

    @Override
    public void kickPlayer(UUID uniqueId, BaseComponent... reason) {
    }

    public MinecraftGroup getMinecraftGroup(String name) {
        return this.minecraftGroups.get(name);
    }

    @Override
    public void deleteMinecraftGroup(MinecraftGroup group) {
        this.deleteMinecraftGroup(group.getName());
    }

    @Override
    public void deleteBungeeGroup(BungeeGroup group) {
        this.deleteBungeeGroup(group.getName());
    }

    @Override
    public void deleteMinecraftGroup(String name) {
        this.groupsConfig.deleteMinecraftGroup(name);
        this.minecraftGroups.remove(name);
        this.networkManager.sendPacketToNodes(new PacketOutMinecraftGroupDeleted(name));
    }

    @Override
    public void deleteBungeeGroup(String name) {
        this.groupsConfig.deleteBungeeGroup(name);
        this.bungeeGroups.remove(name);
        this.networkManager.sendPacketToNodes(new PacketOutBungeeGroupDeleted(name));
    }

    @Override
    public void setDebugging(boolean enabled) {
        this.setDebuggingOnThisComponent(enabled);
        this.networkManager.sendPacketToAll(new PacketOutToggleDebug(enabled));
    }

    @Override
    public boolean isDebugging() {
        return this.logger.isDebugging();
    }

    public void updateMinecraftGroup(MinecraftGroup group) {
        this.minecraftGroups.put(group.getName(), group);
        this.groupsConfig.update(group);
        this.networkManager.sendPacketToNodes(new PacketOutCreateMinecraftGroup(group));
    }

    public void updateBungeeGroup(BungeeGroup group) {
        this.bungeeGroups.put(group.getName(), group);
        this.groupsConfig.update(group);
        this.networkManager.sendPacketToNodes(new PacketOutCreateBungeeGroup(group));
    }

    @Override
    public void updatePlayer(PeepoPlayer player) {
        this.onlinePlayers.put(player.getUniqueId(), player);
        //TODO send packet to other nodes
    }

    @Override
    public int getOnlineCount() {
        return this.onlinePlayers.size();
    }

    public void updateServerInfo(MinecraftServerInfo serverInfo) {
        boolean a = false;
        if (this.processManager.getProcesses().containsKey(serverInfo.getComponentName())) {
            CloudProcess process = this.processManager.getProcesses().get(serverInfo.getComponentName());
            if (process.isServer()) {
                ((ServerProcess) process).setServerInfo(serverInfo);
                a = true;
            }
        }
        if (this.serversOnThisNode.containsKey(serverInfo.getComponentName())) {
            ((MinecraftServerParticipantImpl) this.serversOnThisNode.get(serverInfo.getComponentName())).setServerInfo(serverInfo);
            a = true;
        }

        if (!a) {
            NodeParticipant participant = this.getServerNodes().get(serverInfo.getParentComponentName());
            if (participant != null) {
                participant.sendPacket(new PacketOutUpdateServer(serverInfo));
            }
        } else {
            //TODO send api update packet to all components
        }
    }

    public void updateProxyInfo(BungeeCordProxyInfo proxyInfo) {
        boolean a = false;
        if (this.processManager.getProcesses().containsKey(proxyInfo.getComponentName())) {
            CloudProcess process = this.processManager.getProcesses().get(proxyInfo.getComponentName());
            if (process.isProxy()) {
                ((BungeeProcess) process).setProxyInfo(proxyInfo);
                a = true;
            }
        }
        if (this.proxiesOnThisNode.containsKey(proxyInfo.getComponentName())) {
            ((BungeeCordParticipantImpl) this.proxiesOnThisNode.get(proxyInfo.getComponentName())).setProxyInfo(proxyInfo);
            a = true;
        }

        if (!a) {
            NodeParticipant participant = this.getServerNodes().get(proxyInfo.getParentComponentName());
            if (participant != null) {
                participant.sendPacket(new PacketOutUpdateBungee(proxyInfo));
            }
        } else {
            //TODO send api update packet to all components
        }
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

    public Collection<MinecraftServerInfo> getMinecraftServers() {
        Collection<MinecraftServerInfo> serverInfos = new ArrayList<>();
        this.processManager.getProcesses().values().forEach(process -> {
            if (process.isServer()) {
                serverInfos.add(process.getServerInfo());
            }
        });
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            serverInfos.addAll(value.getServers().values());
            serverInfos.addAll(value.getStartingServers().values());
        }
        return serverInfos;
    }

    public Collection<MinecraftServerInfo> getMinecraftServers(String group) {
        Collection<MinecraftServerInfo> serverInfos = new ArrayList<>();
        this.processManager.getProcesses().values().forEach(process -> {
            if (process.isServer() && process.getGroupName().equalsIgnoreCase(group)) {
                serverInfos.add(process.getServerInfo());
            }
        });
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            for (MinecraftServerInfo serverInfo : value.getServers().values()) {
                if (serverInfo.getGroupName().equalsIgnoreCase(group)) {
                    serverInfos.add(serverInfo);
                }
            }
            for (MinecraftServerInfo serverInfo : value.getStartingServers().values()) {
                if (serverInfo.getGroupName().equalsIgnoreCase(group)) {
                    serverInfos.add(serverInfo);
                }
            }
        }
        return serverInfos;
    }

    public Collection<NodeInfo> getNodeInfos() {
        Collection<NodeInfo> infos = this.connectedNodes.values().stream().map(ClientNodeImpl::getNodeInfo).collect(Collectors.toList());
        infos.add(this.nodeInfo);
        return infos;
    }

    public Collection<MinecraftServerInfo> getStartedMinecraftServers() {
        Collection<MinecraftServerInfo> participants = new ArrayList<>();
        for (MinecraftServerParticipant value : this.serversOnThisNode.values()) {
            participants.add(value.getServerInfo());
        }
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            participants.addAll(value.getServers().values());
        }
        return participants;
    }

    public Collection<MinecraftServerInfo> getStartedMinecraftServers(String group) {
        Collection<MinecraftServerInfo> participants = new ArrayList<>();
        for (MinecraftServerParticipant value : this.serversOnThisNode.values()) {
            if (value.getServerInfo().getGroupName().equalsIgnoreCase(group)) {
                participants.add(value.getServerInfo());
            }
        }
        for (NodeParticipant node : this.networkServer.getConnectedNodes().values()) {
            for (MinecraftServerInfo value : node.getServers().values()) {
                if (value.getGroupName().equalsIgnoreCase(group)) {
                    participants.add(value);
                }
            }
        }
        return participants;
    }

    public Collection<BungeeCordProxyInfo> getBungeeProxies() {
        Collection<BungeeCordProxyInfo> serverInfos = new ArrayList<>();
        this.processManager.getProcesses().values().forEach(process -> {
            if (process.isProxy()) {
                serverInfos.add(process.getProxyInfo());
            }
        });
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            serverInfos.addAll(value.getProxies().values());
            serverInfos.addAll(value.getStartingProxies().values());
        }
        return serverInfos;
    }

    public Collection<BungeeCordProxyInfo> getBungeeProxies(String group) {
        Collection<BungeeCordProxyInfo> serverInfos = new ArrayList<>();
        this.processManager.getProcesses().values().forEach(process -> {
            if (process.isProxy() && process.getGroupName().equalsIgnoreCase(group)) {
                serverInfos.add(process.getProxyInfo());
            }
        });
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            for (BungeeCordProxyInfo serverInfo : value.getProxies().values()) {
                if (serverInfo.getGroupName().equalsIgnoreCase(group)) {
                    serverInfos.add(serverInfo);
                }
            }
            for (BungeeCordProxyInfo serverInfo : value.getStartingProxies().values()) {
                if (serverInfo.getGroupName().equalsIgnoreCase(group)) {
                    serverInfos.add(serverInfo);
                }
            }
        }
        return serverInfos;
    }

    public Collection<BungeeCordProxyInfo> getStartedBungeeProxies() {
        Collection<BungeeCordProxyInfo> participants = new ArrayList<>();
        for (BungeeCordParticipant value : this.proxiesOnThisNode.values()) {
            participants.add(value.getProxyInfo());
        }
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            participants.addAll(value.getProxies().values());
        }
        return participants;
    }

    public Collection<BungeeCordProxyInfo> getStartedBungeeProxies(String group) {
        Collection<BungeeCordProxyInfo> participants = new ArrayList<>();
        for (BungeeCordParticipant value : this.proxiesOnThisNode.values()) {
            if (value.getProxyInfo().getGroupName().equalsIgnoreCase(group)) {
                participants.add(value.getProxyInfo());
            }
        }
        for (NodeParticipant node : this.networkServer.getConnectedNodes().values()) {
            for (BungeeCordProxyInfo value : node.getProxies().values()) {
                if (value.getGroupName().equalsIgnoreCase(group)) {
                    participants.add(value);
                }
            }
        }
        return participants;
    }

    public int getNextServerId(String group) {
        AtomicInteger i = new AtomicInteger();
        this.networkServer.getConnectedNodes().values().forEach(participant -> {
            i.addAndGet(Math.toIntExact(participant.getServers().values().stream().filter(serverInfo -> serverInfo.getGroupName().equals(group)).count()));
            i.addAndGet(Math.toIntExact(participant.getStartingServers().values().stream().filter(serverInfo -> serverInfo.getGroupName().equals(group)).count()));
            i.addAndGet(Math.toIntExact(participant.getWaitingServers().values().stream().filter(serverInfo -> serverInfo.getGroupName().equals(group)).count()));
        });
        return i.get() + this.processManager.getProcessesOfMinecraftGroup(group).size() + this.processManager.getProcessesOfMinecraftGroupQueued(group).size() + 1;
    }

    public int getNextProxyId(String group) {
        AtomicInteger i = new AtomicInteger();
        this.networkServer.getConnectedNodes().values().forEach(participant -> {
            i.addAndGet(Math.toIntExact(participant.getProxies().values().stream().filter(serverInfo -> serverInfo.getGroupName().equals(group)).count()));
            i.addAndGet(Math.toIntExact(participant.getStartingProxies().values().stream().filter(serverInfo -> serverInfo.getGroupName().equals(group)).count()));
            i.addAndGet(Math.toIntExact(participant.getWaitingProxies().values().stream().filter(serverInfo -> serverInfo.getGroupName().equals(group)).count()));
        });
        return i.get() + this.processManager.getProcessesOfBungeeGroup(group).size() + this.processManager.getProcessesOfBungeeGroupQueued(group).size() + 1;
    }

    public boolean isServerStarted(String name) {
        if (this.serversOnThisNode.containsKey(name))
            return true;
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            if (value.getServers().containsKey(name) || value.getWaitingServers().containsKey(name) || value.getStartingServers().containsKey(name)) {
                return true;
            }
        }
        if ((this.processManager.getProcesses().containsKey(name) && this.processManager.getProcesses().get(name).isServer())
                || this.processManager.getServerQueue().getServerProcesses().stream().anyMatch(process -> process.getName().equals(name)))
            return true;
        return false;
    }

    public boolean isProxyStarted(String name) {
        if (this.proxiesOnThisNode.containsKey(name))
            return true;
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            if (value.getProxies().containsKey(name) || value.getWaitingProxies().containsKey(name) || value.getStartingProxies().containsKey(name)) {
                return true;
            }
        }
        if ((this.processManager.getProcesses().containsKey(name) && this.processManager.getProcesses().get(name).isProxy()) ||
                this.processManager.getServerQueue().getServerProcesses().stream().anyMatch(process -> process.getName().equals(name))) {
            return true;
        }
        return false;
    }

    public QueryRequest<OSProcess> getProcessOfServerInfo(MinecraftServerInfo serverInfo) {
        return this.getProcessByPid(serverInfo.getParentComponentName(), serverInfo.getPid());
    }

    public QueryRequest<OSProcess> getProcessOfProxyInfo(BungeeCordProxyInfo proxyInfo) {
        return this.getProcessByPid(proxyInfo.getParentComponentName(), proxyInfo.getPid());
    }

    private QueryRequest<OSProcess> getProcessByPid(String parentComponentName, int pid) {
        if (pid == -1)
            return new DirectQueryRequest<>(null);
        if (parentComponentName.equals(this.nodeInfo.getName()))
            return new DirectQueryRequest<>(this.systemInfo.getOperatingSystem().getProcess(pid));
        ClientNode node = this.getConnectedNode(parentComponentName);
        if (node == null)
            return new DirectQueryRequest<>(null);
        return this.packetManager.packetQueryAsync(node, new PacketOutQueryProcessInfo(pid), packet -> {
            if (packet instanceof SerializationPacket) {
                PacketSerializable packetSerializable = ((SerializationPacket) packet).getSerializable();
                if (packetSerializable instanceof OSProcess)
                    return (OSProcess) packetSerializable;
                if (packetSerializable instanceof PacketSerializableWrapper && ((PacketSerializableWrapper) packetSerializable).getSerializable() instanceof OSProcess)
                    return (OSProcess) ((PacketSerializableWrapper) packetSerializable).getSerializable();
            }
            return null;
        });
    }

    public MinecraftServerInfo getMinecraftServerInfo(String name) {
        if (this.processManager.getProcesses().containsKey(name)) {
            CloudProcess process = this.processManager.getProcesses().get(name);
            if (process.isServer())
                return process.getServerInfo();
        }
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            if (value.getServers().containsKey(name))
                return value.getServers().get(name);
            if (value.getWaitingServers().containsKey(name))
                return value.getWaitingServers().get(name);
            if (value.getStartingServers().containsKey(name))
                return value.getStartingServers().get(name);
        }
        return null;
    }

    public BungeeCordProxyInfo getBungeeProxyInfo(String name) {
        if (this.processManager.getProcesses().containsKey(name)) {
            CloudProcess process = this.processManager.getProcesses().get(name);
            if (process.isProxy())
                return ((BungeeProcess) process).getProxyInfo();
        }
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            if (value.getProxies().containsKey(name))
                return value.getProxies().get(name);
            if (value.getWaitingProxies().containsKey(name))
                return value.getWaitingProxies().get(name);
            if (value.getStartingProxies().containsKey(name))
                return value.getStartingProxies().get(name);
        }
        return null;
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group) {
        return this.startMinecraftServer(group, group.getMemory());
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, int memory) {
        int id = this.getNextServerId(group.getName());
        return this.startMinecraftServer(group, group.getName() + "-" + id, id, memory);
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name) {
        return this.startMinecraftServer(group, name, group.getMemory());
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name, int id, int memory) {
        return this.startMinecraftServer(this.getBestNodeInfo(memory), group, name, id, memory);
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name, int memory) {
        return startMinecraftServer(this.getBestNodeInfo(memory), group, name, memory);
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group) {
        return this.startMinecraftServer(nodeInfo, group, group.getMemory());
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, int memory) {
        int id = this.getNextServerId(group.getName());
        return this.startMinecraftServer(nodeInfo, group, group.getName() + "-" + id, id, memory);
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name) {
        return this.startMinecraftServer(nodeInfo, group, name, this.getNextServerId(group.getName()), group.getMemory());
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int memory) {
        return this.startMinecraftServer(nodeInfo, group, name, this.getNextServerId(group.getName()), memory);
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int id, int memory) {
        if (!this.running)
            return null;

        if (this.getNextServerId(group.getName()) - 1 >= group.getMaxServers() || this.isServerStarted(name))
            return null;
        if (nodeInfo == null) //not enough ram on any node free
            return null;

        Template template = this.findTemplate(group);
        if (template == null)
            return null;

        if (this.nodeInfo.getName().equals(nodeInfo.getName())) {
            MinecraftServerInfo serverInfo = new MinecraftServerInfo(
                    name,
                    group.getName(),
                    id,
                    this.nodeInfo.getName(),
                    memory,
                    this.cloudConfig.getHost().getHost(),
                    this.findServerPort(group),
                    -1,
                    group.getMaxPlayers(),
                    group.getMotd(),
                    MinecraftState.OFFLINE,
                    new HashMap<>(),
                    template,
                    -1L
            );
            this.processManager.getServerQueue().queueProcess(this.processManager.getServerQueue().createProcess(serverInfo), false);
            return serverInfo;
        } else {
            NodeParticipant channel = this.getServerNodes().get(nodeInfo.getName());
            if (channel == null)
                return null;

            MinecraftServerInfo serverInfo = new MinecraftServerInfo(
                    name,
                    group.getName(),
                    id,
                    nodeInfo.getName(),
                    memory,
                    channel.getAddress(),
                    this.findServerPort(group),
                    -1,
                    group.getMaxPlayers(),
                    group.getMotd(),
                    MinecraftState.OFFLINE,
                    new HashMap<>(),
                    template,
                    -1L
            );
            channel.startMinecraftServer(serverInfo);

            return serverInfo;
        }
    }

    public void startMinecraftServer(MinecraftServerInfo serverInfo) {
        if (!this.running)
            return;

        MinecraftGroup group = this.getMinecraftGroup(serverInfo.getGroupName());
        if (group == null)
            return;
        if (this.getNextServerId(serverInfo.getGroupName()) - 1 >= group.getMaxServers() || this.isServerStarted(serverInfo.getComponentName()))
            return;

        if (this.nodeInfo.getName().equals(serverInfo.getParentComponentName())) {
            this.processManager.getServerQueue().queueProcess(this.processManager.getServerQueue().createProcess(serverInfo), false);
        } else {
            NodeParticipant channel = this.getServerNodes().get(serverInfo.getParentComponentName());
            if (channel == null)
                return;

            channel.startMinecraftServer(serverInfo);
        }
    }

    public void stopBungeeProxy(String name) {
        //TODO
    }

    public void stopBungeeProxy(BungeeCordProxyInfo proxyInfo) {
        //TODO
    }


    public void stopMinecraftServer(String name) {
        //TODO
    }

    public void stopMinecraftServer(MinecraftServerInfo serverInfo) {
        //TODO
    }


    public void stopBungeeGroup(String name) {

    }

    public void stopMinecraftGroup(String name) {

    }

    public void stopBungeeGroup(BungeeGroup group) {

    }


    public void stopMinecraftGroup(MinecraftGroup group) {

    }

    public BungeeCordProxyInfo startBungeeProxy(BungeeGroup group) {
        return this.startBungeeProxy(group, group.getMemory());
    }

    public BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, int memory) {
        int id = this.getNextProxyId(group.getName());
        return this.startBungeeProxy(group, group.getName() + "-" + id, id, memory);
    }

    public BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, String name) {
        return this.startBungeeProxy(group, name, group.getMemory());
    }

    public BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, String name, int id, int memory) {
        return this.startBungeeProxy(this.getBestNodeInfo(memory), group, name, id, memory);
    }

    public BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, String name, int memory) {
        return startBungeeProxy(this.getBestNodeInfo(memory), group, name, memory);
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group) {
        return this.startBungeeProxy(nodeInfo, group, group.getMemory());
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, int memory) {
        int id = this.getNextProxyId(group.getName());
        return this.startBungeeProxy(nodeInfo, group, group.getName() + "-" + id, id, memory);
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name) {
        return this.startBungeeProxy(nodeInfo, group, name, this.getNextProxyId(group.getName()), group.getMemory());
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name, int memory) {
        return this.startBungeeProxy(nodeInfo, group, name, this.getNextServerId(group.getName()), memory);
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name, int id, int memory) {
        if (!this.running)
            return null;

        int nextId = this.getNextProxyId(group.getName()) - 1;
        if (nextId >= group.getMaxServers() || this.isProxyStarted(name))
            return null;
        if (nodeInfo == null) //not enough ram on any node free
            return null;

        Template template = this.findTemplate(group);
        if (template == null)
            return null;

        if (this.nodeInfo.getName().equals(nodeInfo.getName())) {
            BungeeCordProxyInfo serverInfo = new BungeeCordProxyInfo(
                    name,
                    group.getName(),
                    id,
                    this.nodeInfo.getName(),
                    memory,
                    this.cloudConfig.getHost().getHost(),
                    group.getStartPort() + nextId,
                    -1,
                    new HashMap<>(),
                    template,
                    -1L
            );
            this.processManager.getServerQueue().queueProcess(this.processManager.getServerQueue().createProcess(serverInfo), false);
            return serverInfo;
        } else {
            NodeParticipant channel = this.getServerNodes().get(nodeInfo.getName());
            if (channel == null)
                return null;

            BungeeCordProxyInfo serverInfo = new BungeeCordProxyInfo(
                    name,
                    group.getName(),
                    id,
                    nodeInfo.getName(),
                    memory,
                    channel.getAddress(),
                    group.getStartPort() + nextId,
                    -1,
                    new HashMap<>(),
                    template,
                    -1L
            );

            channel.startBungeeCordProxy(serverInfo);

            return serverInfo;
        }
    }

    public void startBungeeProxy(BungeeCordProxyInfo proxyInfo) {
        if (!this.running)
            return;

        if (this.nodeInfo.getName().equals(proxyInfo.getParentComponentName())) {
            this.processManager.getServerQueue().queueProcess(this.processManager.getServerQueue().createProcess(proxyInfo), false);
        } else {
            NodeParticipant channel = this.getServerNodes().get(proxyInfo.getParentComponentName());
            if (channel == null)
                return;

            channel.startBungeeCordProxy(proxyInfo);
        }
    }

    public Template findTemplate(MinecraftGroup group) {
        if (group.getTemplates().isEmpty())
            return null;
        return group.getTemplates().get(ThreadLocalRandom.current().nextInt(group.getTemplates().size()));
    }

    public Template findTemplate(BungeeGroup group) {
        if (group.getTemplates().isEmpty())
            return null;
        return group.getTemplates().get(ThreadLocalRandom.current().nextInt(group.getTemplates().size()));
    }

    public Collection<Integer> getBoundPorts() {
        Collection<Integer> a = this.processManager.getProcesses().values().stream().map(CloudProcess::getPort).collect(Collectors.toList());
        for (CloudProcess serverProcess : this.processManager.getServerQueue().getServerProcesses()) {
            a.add(serverProcess.getPort());
        }
        return a;
    }

    private int findServerPort(MinecraftGroup group) {
        Collection<Integer> boundPorts = this.getBoundPorts();
        int port = group.getStartPort();
        while (boundPorts.contains(port)) {
            port += ThreadLocalRandom.current().nextInt(15);
        }
        return port;
    }

}