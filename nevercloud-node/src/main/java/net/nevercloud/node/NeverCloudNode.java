package net.nevercloud.node;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import jline.console.ConsoleReader;
import lombok.Getter;
import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.network.auth.Auth;
import net.nevercloud.lib.network.auth.NetworkComponentType;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.PacketInfo;
import net.nevercloud.lib.network.packet.PacketManager;
import net.nevercloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.nevercloud.lib.node.NodeInfo;
import net.nevercloud.lib.server.Template;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.lib.server.bungee.BungeeGroup;
import net.nevercloud.lib.server.minecraft.MinecraftGroup;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.lib.utility.network.NetworkAddress;
import net.nevercloud.node.addon.AddonManager;
import net.nevercloud.node.addon.defaults.DefaultAddonManager;
import net.nevercloud.node.addon.node.NodeAddon;
import net.nevercloud.node.api.event.internal.EventManager;
import net.nevercloud.node.command.CommandManager;
import net.nevercloud.node.command.CommandSender;
import net.nevercloud.node.command.defaults.*;
import net.nevercloud.node.database.DatabaseLoader;
import net.nevercloud.node.database.DatabaseManager;
import net.nevercloud.node.languagesystem.LanguagesManager;
import net.nevercloud.node.logging.ColoredLogger;
import net.nevercloud.node.logging.ConsoleColor;
import net.nevercloud.node.network.ClientNode;
import net.nevercloud.node.network.NetworkServer;
import net.nevercloud.node.network.packet.in.PacketInUpdateNodeInfo;
import net.nevercloud.node.network.packet.out.PacketOutUpdateNodeInfo;
import net.nevercloud.node.network.packet.out.group.PacketOutCreateBungeeGroup;
import net.nevercloud.node.network.packet.out.group.PacketOutCreateMinecraftGroup;
import net.nevercloud.node.network.participant.BungeeCordParticipant;
import net.nevercloud.node.network.participant.MinecraftServerParticipant;
import net.nevercloud.node.network.participant.NodeParticipant;
import net.nevercloud.node.screen.ScreenManager;
import net.nevercloud.node.server.ServerFilesLoader;
import net.nevercloud.node.server.process.ProcessManager;
import net.nevercloud.node.statistic.StatisticsManager;
import net.nevercloud.node.updater.AutoUpdaterManager;
import net.nevercloud.node.updater.UpdateCheckResponse;
import net.nevercloud.node.utility.NodeUtils;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

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
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);

    private AutoUpdaterManager autoUpdaterManager;

    private AddonManager<NodeAddon> nodeAddonManager;
    private DefaultAddonManager defaultAddonManager = new DefaultAddonManager();

    private String networkAuthKey;
    private NetworkServer networkServer;
    private Map<String, ClientNode> connectedNodes = new HashMap<>();
    private Map<String, NodeParticipant> serverNodes = new HashMap<>();

    private PacketManager packetManager = new PacketManager();

    private ScreenManager screenManager = new ScreenManager();

    private EventManager eventManager;

    private StatisticsManager statisticsManager = new StatisticsManager();

    private NodeInfo nodeInfo;

    private CloudConfig cloudConfig;
    private GroupsConfig groupsConfig = new GroupsConfig();

    private Map<String, MinecraftGroup> minecraftGroups;
    private Map<String, BungeeGroup> bungeeGroups;

    private ProcessManager processManager;

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

    private String lastUniqueId;

    private boolean running = true;

    NeverCloudNode() throws IOException {
        Preconditions.checkArgument(instance == null, "instance is already defined");
        instance = this;

        try {
            Field field = Charset.class.getDeclaredField("defaultCharset");
            field.setAccessible(true);
            field.set(null, StandardCharsets.UTF_8);
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }

        ConsoleReader consoleReader = new ConsoleReader(System.in, System.out);
        this.logger = new ColoredLogger(consoleReader);

        {
            try {
                URLConnection connection = new URL(SystemUtils.CENTRAL_SERVER_URL + "banned").openConnection();
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
                    System.out.println("&cOur NeverCloud UpdateServer (&e" + SystemUtils.CENTRAL_SERVER_URL + "&c) is currently offline, some functions won't work, we'll try to get it online as soon as possible");
                }
            }
        }

        this.internalConfig = SimpleJsonObject.load("internal/internalData.json");

        if (!this.internalConfig.contains("acceptedInformationsSendingToServer")) {
            System.out.println("&4Do you accept, that some data of your cloud and server (the ip address hashed, the os, the version of the cloud, the uniqueId of your cloud, the maximum amount of memory of your cloud and the cpu cores) will be send to our server and saved there? We won't share the hashed ip address and the unique id of your cloud, but the other information are important for the support. Please type &eyes&4, if you agree, but please, you won't get any support if you do not agree to this, because we need information about your system to help you.");
            String s = this.logger.readLine();
            if (s.equalsIgnoreCase("yes")) {
                System.out.println("&aYou have accepted that the data named above will be saved on our server.");
                this.internalConfig.append("acceptedInformationsSendingToServer", true);
                this.saveInternalConfigFile();
            } else {
                System.out.println("&cYou have not accepted that the data named above will be send to our server. You won't get any support for the system.");
            }
        } else if (!this.internalConfig.getBoolean("acceptedInformationsSendingToServer")) {
            System.out.println("&cYou have not accepted that the data named above will be send to our server. You won't get any support for the system.");
        } else {
            System.out.println("&aYou have accepted that the ip address of your server hashed, the os, the version of the cloud, the uniqueId of your cloud, the maximum amount of memory of your cloud and the cpu cores will be saved on our server.");
        }

        this.languagesManager = new LanguagesManager();

        this.loadConfigs();

        this.databaseLoader = new DatabaseLoader("databaseAddons");
        this.databaseManager = this.databaseLoader.loadDatabaseManager(this);

        NodeUtils.updateNodeInfoForSupport(null);

        this.nodeInfo = this.cloudConfig.loadNodeInfo(0);

        this.autoUpdaterManager = new AutoUpdaterManager();

        this.commandManager = new CommandManager(this.logger);

        this.initCommands(this.commandManager);
        this.initPacketHandlers();

        this.eventManager = new EventManager();

        this.nodeAddonManager = new AddonManager<>();

        this.installUpdatesSync(this.commandManager.getConsole());

        this.executorService.execute(() -> {
            Thread thread = Thread.currentThread();
            while (!thread.isInterrupted()) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.nodeInfo.setUsedMemory(this.getMemoryUsedOnThisInstance());
                this.nodeInfo.setCpuUsage(SystemUtils.cpuUsageProcess());
                this.sendPacketToNodes(new PacketOutUpdateNodeInfo(this.nodeInfo));
            }
        });

        this.minecraftGroups = this.groupsConfig.loadMinecraftGroups();
        this.bungeeGroups = this.groupsConfig.loadBungeeGroups();

        this.processManager = new ProcessManager(
                bungee -> this.memoryUsedOnThisInstanceByBungee += bungee,
                server -> this.memoryUsedOnThisInstanceByServer += server,
                this.cloudConfig
        );

        ServerFilesLoader.tryInstallSpigot();
        ServerFilesLoader.tryInstallBungee();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown0));

        this.reloadAddons();
    }

    private void initPacketHandlers() {
        this.packetManager.registerPacket(new PacketInfo(14, PacketInUpdateNodeInfo.class, new PacketInUpdateNodeInfo()));
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
                new CommandClear(),
                new CommandCreate(),
                new CommandStart(),
                new CommandScreen(),
                new CommandUnique(),
                new CommandSupportUpdate()
        );
    }

    private void shutdown0() {
        running = false;
        if (this.processManager != null)
            this.processManager.shutdown();
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
                    : new InetSocketAddress(this.cloudConfig.getHost().getHost(), this.cloudConfig.getHost().getPort()), this.packetManager);
            this.networkServer.run();
        }

        if (oldNodes == null || !oldNodes.equals(newNodes)) {
            if (oldNodes == null) {
                oldNodes = newNodes;
                for (NetworkAddress node : oldNodes) {
                    if (node.getHost() == null || node.getHost().equalsIgnoreCase("host"))
                        continue;
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
                    if (node.getHost() == null || node.getHost().equalsIgnoreCase("host"))
                        continue;
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

    /**
     * Gets the uniqueId of the network (used for example for the support)
     * @param consumer the consumer to post the uniqueId to
     */
    public void getUniqueId(Consumer<String> consumer) {
        this.databaseManager.getDatabase("internal_configs").get("unique", simpleJsonObject -> {
            String unique;
            if (simpleJsonObject == null) {
                unique = SystemUtils.randomString(128);
                this.databaseManager.getDatabase("internal_configs").insert("unique", new SimpleJsonObject().append("unique", unique));
            } else {
                unique = simpleJsonObject.getString("unique");
                if (this.lastUniqueId == null)
                    this.lastUniqueId = unique;
                if (!this.lastUniqueId.equals(unique)) {
                    simpleJsonObject.append("unique", this.lastUniqueId);
                    this.databaseManager.getDatabase("internal_configs").update("unique", simpleJsonObject);
                }
            }
            consumer.accept(unique);
        });
    }

    /**
     * Gets the max memory of all nodes in the network
     * @return the memory of all nodes in the network
     */
    public int getMaxMemory() {
        int maxMemory = this.cloudConfig.getMaxMemory();
        for (ClientNode value : this.connectedNodes.values()) {
            if (value.getNodeInfo() != null)
                maxMemory += value.getNodeInfo().getMaxMemory();
        }
        return maxMemory;
    }

    private void connectToNode(NetworkAddress node) {
        ClientNode client = new ClientNode(new InetSocketAddress(node.getHost(), node.getPort()), this.packetManager, new ChannelHandlerAdapter(),
                new Auth(this.networkAuthKey, this.cloudConfig.getNodeName(), NetworkComponentType.NODE, null, new SimpleJsonObject().append("nodeInfo", this.nodeInfo)),
                null);
        this.connectedNodes.put(node.getHost(), client);
        new Thread(client, "Node client @" + node.toString()).start();
    }

    /**
     * Checks asynchronous for updates and installs them
     * @param sender the sender to which the messages should be send
     */
    public void installUpdates(CommandSender sender) {
        NeverCloudNode.getInstance().getAutoUpdaterManager().checkUpdates(updateCheckResponse -> this.installUpdates0(sender, updateCheckResponse));
    }

    /**
     * Checks synchronous for updates and installs them
     * @param sender the sender to which the messages should be send
     */
    public void installUpdatesSync(CommandSender sender) {
        this.installUpdates0(sender, NeverCloudNode.getInstance().getAutoUpdaterManager().checkUpdatesSync());
    }

    private void installUpdates0(CommandSender sender, UpdateCheckResponse updateCheckResponse) {
        if (updateCheckResponse != null) {
            if (updateCheckResponse.isUpToDate()) {
                sender.sendMessageLanguageKey("autoupdate.upToDate");
            } else {
                sender.createLanguageMessage("autoupdate.versionsBehind").replace("%versionsBehind%", String.valueOf(updateCheckResponse.getVersionsBehind())
                        .replace("%newestVersion%", updateCheckResponse.getNewestVersion())).send(); //TODO %newestVersion% is not replaced with the version
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
    }

    /**
     * Shuts down the system
     */
    public void shutdown() {
        shutdown0();
        System.exit(0);
    }

    /**
     * Reloads all the configs, addons, etc. of the system
     */
    public void reload() {
        this.reloadAddons();
        this.reloadConfigs();
    }

    /**
     * Reloads all configs of the system
     */
    public void reloadConfigs() {
        this.loadConfigs();
        this.nodeInfo = this.cloudConfig.loadNodeInfo(this.getMemoryUsedOnThisInstance());
    }

    /**
     * Reloads all addons of the system
     */
    public void reloadAddons() {
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

    /**
     * Gets the local address of the server
     * @return the local address or if it could not be detected "could not detect local address"
     */
    public String getLocalAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            return "could not detect local address";
        }
    }

    /**
     * The amount of memory used of all the servers and proxies on this node instance
     * @return the memory used on this instance in MB
     */
    public int getMemoryUsedOnThisInstance() {
        return this.memoryUsedOnThisInstanceByBungee + this.memoryUsedOnThisInstanceByServer + this.processManager.getServerQueue().getMemoryNeededForProcessesInQueue();
    }

    /**
     * Saves the internal config
     */
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

    /**
     * Gets a node connected to this node as a client
     * @param name the name of the node
     * @return the connected node or null, if no node with the given {@code name} is connected
     */
    public ClientNode getConnectedNode(String name) {
        return this.connectedNodes.get(name);
    }


    public BungeeGroup getBungeeGroup(String name) {
        return this.bungeeGroups.get(name);
    }

    public MinecraftGroup getMinecraftGroup(String name) {
        return this.minecraftGroups.get(name);
    }

    public void sendPacketToNodes(Packet packet) {
        this.connectedNodes.values().forEach(networkClient -> networkClient.sendPacket(packet));
    }

    public void updateMinecraftGroup(MinecraftGroup group) {
        this.minecraftGroups.put(group.getName(), group);
        this.sendPacketToNodes(new PacketOutCreateMinecraftGroup(group));
    }

    public void updateBungeeGroup(BungeeGroup group) {
        this.bungeeGroups.put(group.getName(), group);
        this.sendPacketToNodes(new PacketOutCreateBungeeGroup(group));
    }

    public void updateServerInfo(MinecraftServerInfo serverInfo) {
        //TODO
    }

    public void updateProxyInfo(BungeeCordProxyInfo proxyInfo) {
        //TODO
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
        Collection<MinecraftServerInfo> participants = new ArrayList<>();
        for (MinecraftServerParticipant value : this.serversOnThisNode.values()) {
            participants.add(value.getServerInfo());
        }
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            participants.addAll(value.getServers().values());
        }
        return participants;
    }

    public Collection<MinecraftServerInfo> getMinecraftServers(String group) {
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
        Collection<BungeeCordProxyInfo> participants = new ArrayList<>();
        for (BungeeCordParticipant value : this.proxiesOnThisNode.values()) {
            participants.add(value.getProxyInfo());
        }
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            participants.addAll(value.getProxies().values());
        }
        return participants;
    }

    public Collection<BungeeCordProxyInfo> getBungeeProxies(String group) {
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
        this.networkServer.getConnectedNodes().values().forEach(participant -> i.addAndGet(participant.getServers().size() + participant.getStartingServers().size() + participant.getWaitingServers().size()));
        return i.get() + this.processManager.getProcessesOfMinecraftGroup(group).size() + this.processManager.getProcessesOfMinecraftGroupQueued(group).size() + 1;
    }

    public boolean isServerStarted(String name) {
        if (this.getMinecraftServers().stream().anyMatch(serverInfo -> serverInfo.getComponentName().equals(name)))
            return true;
        for (NodeParticipant value : this.networkServer.getConnectedNodes().values()) {
            if (value.getServers().containsKey(name) || value.getWaitingServers().containsKey(name) || value.getStartingServers().containsKey(name)) {
                return true;
            }
        }
        if (this.processManager.getProcesses().containsKey(name) || this.processManager.getServerQueue().getServerProcesses().stream().anyMatch(process -> process.getName().equals(name)))
            return true;
        return false;
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group) {
        return this.startMinecraftServer(group, group.getMemory());
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, int memory) {
        int id = this.getNextServerId(group.getName());
        return this.startMinecraftServer(group, group.getName() + "-" + id, id, memory);
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name) {
        return this.startMinecraftServer(group, group.getName() + "-" + this.getNextServerId(group.getName()), group.getMemory());
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
        if (this.getNextServerId(group.getName()) - 1 >= group.getMaxServers() || this.isServerStarted(name))
            return null;
        if (nodeInfo == null)
            nodeInfo = this.nodeInfo;

        if (this.nodeInfo.getName().equals(nodeInfo.getName())) {
            MinecraftServerInfo serverInfo = new MinecraftServerInfo(
                    name,
                    group.getName(),
                    id,
                    this.nodeInfo.getName(),
                    memory,
                    new HashMap<>(),
                    this.findTemplate(group),
                    -1L
            );
            this.processManager.getServerQueue().queueProcess(this.processManager.getServerQueue().createProcess(serverInfo), false);
            return serverInfo;
        } else {

        }
        return null;
    }

    public Template findTemplate(MinecraftGroup group) {
        return group.getTemplates().get(ThreadLocalRandom.current().nextInt(group.getTemplates().size()));
    }

}
