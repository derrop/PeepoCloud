package net.peepocloud.plugin;

import net.md_5.bungee.api.chat.BaseComponent;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.in.PacketInToggleDebug;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.lib.scheduler.Scheduler;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.utility.network.QueryRequest;
import net.peepocloud.plugin.api.PeepoCloudPluginAPI;
import net.peepocloud.plugin.bungee.PeepoBungeePlugin;
import net.peepocloud.plugin.bukkit.PeepoBukkitPlugin;
import net.peepocloud.plugin.api.network.handler.NetworkAPIHandler;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkClient;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.peepocloud.lib.utility.network.NetworkAddress;
import net.peepocloud.plugin.network.packet.in.server.PacketInAPIProxyStarted;
import net.peepocloud.plugin.network.packet.in.server.PacketInAPIProxyStopped;
import net.peepocloud.plugin.network.packet.in.server.PacketInAPIServerStarted;
import net.peepocloud.plugin.network.packet.in.server.PacketInAPIServerStopped;
import net.peepocloud.plugin.network.packet.out.PacketOutAPIQueryGroups;
import net.peepocloud.plugin.network.packet.out.PacketOutAPIQueryProxyInfos;
import net.peepocloud.plugin.network.packet.out.PacketOutAPIQueryServerInfos;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public abstract class PeepoCloudPlugin extends PeepoCloudPluginAPI {
    private static PeepoCloudPlugin instance;

    protected PacketManager packetManager = new PacketManager();
    protected NetworkClient nodeConnector;
    protected Collection<NetworkAPIHandler> networkHandlers = new ArrayList<>();
    protected Scheduler scheduler = new Scheduler();
    private boolean debugging = false;

    private Map<String, MinecraftGroup> minecraftGroups;
    private Map<String, BungeeGroup> bungeeGroups;

    public PeepoCloudPlugin(Path nodeInfoFile) {
        instance = this;
        PeepoCloudPlugin.setInstance(this);

        if (nodeInfoFile == null) {
            this.shutdown();
            throw new NullPointerException("nodeInfoFile not specified");
        }
        SimpleJsonObject nodeInfo = SimpleJsonObject.load(nodeInfoFile);
        this.nodeConnector = new NetworkClient(nodeInfo.getObject("networkAddress", NetworkAddress.class)
                .toInetSocketAddress(), this.packetManager, new ChannelHandlerAdapter(), nodeInfo.getObject("auth", Auth.class));
        this.nodeConnector.setExceptionTask(this::shutdown);

        // deleting the file because the info has been read
        try {
            Files.delete(nodeInfoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bootstrap() {
        this.scheduler.getThreadPool().execute(this.scheduler);

        this.packetManager.registerPacket(new PacketInAPIServerStarted());
        this.packetManager.registerPacket(new PacketInAPIServerStopped());
        this.packetManager.registerPacket(new PacketInAPIProxyStarted());
        this.packetManager.registerPacket(new PacketInAPIProxyStopped());
        this.packetManager.registerPacket(new PacketInToggleDebug());

        this.nodeConnector.run();
    }


    @Override
    public void shutdown() {
        this.nodeConnector.shutdown();
        this.scheduler.disable();
    }


    @Override
    public abstract boolean isBungee();

    @Override
    public abstract boolean isBukkit();

    @Override
    public PeepoBukkitPlugin toBukkit() {
        if (this.isBukkit())
            return (PeepoBukkitPlugin) this;
        throw new UnsupportedOperationException("This instance does not support bukkit");
    }

    @Override
    public PeepoBungeePlugin toBungee() {
        if (this.isBungee())
            return (PeepoBungeePlugin) this;
        throw new UnsupportedOperationException("This instance does not support bungeecord");
    }

    @Override
    public void setDebuggingOnThisComponent(boolean enable) {
        this.debugging = enable;
    }

    @Override
    public void debug(String message) {
        if (this.debugging) {
            System.out.println("&5[DEBUG] " + message);
        }
    }

    @Override
    public void registerNetworkHandler(NetworkAPIHandler handler) {
        this.networkHandlers.add(handler);
    }

    @Override
    public boolean unregisterNetworkHandler(NetworkAPIHandler handler) {
        return this.networkHandlers.remove(handler);
    }

    @Override
    public boolean isServerStarted(String name) {
        return false;
    }

    @Override
    public boolean isProxyStarted(String name) {
        return false;
    }

    @Override
    public QueryRequest<MinecraftServerInfo> getMinecraftServerInfo(String name) {
        return null;
    }

    @Override
    public QueryRequest<BungeeCordProxyInfo> getBungeeProxyInfo(String name) {
        return null;
    }

    @Override
    public QueryRequest<MinecraftServerInfo> startMinecraftServer(MinecraftGroup group) {
        return null;
    }

    @Override
    public QueryRequest<MinecraftServerInfo> startMinecraftServer(MinecraftGroup group, int memory) {
        return null;
    }

    @Override
    public QueryRequest<MinecraftServerInfo> startMinecraftServer(MinecraftGroup group, String name) {
        return null;
    }

    @Override
    public QueryRequest<MinecraftServerInfo> startMinecraftServer(MinecraftGroup group, String name, int id, int memory) {
        return null;
    }

    @Override
    public QueryRequest<MinecraftServerInfo> startMinecraftServer(MinecraftGroup group, String name, int memory) {
        return null;
    }

    @Override
    public QueryRequest<MinecraftServerInfo> startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group) {
        return null;
    }

    @Override
    public QueryRequest<MinecraftServerInfo> startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, int memory) {
        return null;
    }

    @Override
    public QueryRequest<MinecraftServerInfo> startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name) {
        return null;
    }

    @Override
    public QueryRequest<MinecraftServerInfo> startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int memory) {
        return null;
    }

    @Override
    public QueryRequest<MinecraftServerInfo> startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int id, int memory) {
        return null;
    }

    @Override
    public void startMinecraftServer(MinecraftServerInfo serverInfo) {

    }

    @Override
    public void stopBungeeProxy(String name) {

    }

    @Override
    public void stopBungeeProxy(BungeeCordProxyInfo proxyInfo) {

    }

    @Override
    public void stopMinecraftServer(String name) {

    }

    @Override
    public void stopMinecraftServer(MinecraftServerInfo serverInfo) {

    }

    @Override
    public void stopBungeeGroup(String name) {

    }

    @Override
    public void stopMinecraftGroup(String name) {

    }

    @Override
    public QueryRequest<BungeeCordProxyInfo> startBungeeProxy(BungeeGroup group) {
        return null;
    }

    @Override
    public QueryRequest<BungeeCordProxyInfo> startBungeeProxy(BungeeGroup group, int memory) {
        return null;
    }

    @Override
    public QueryRequest<BungeeCordProxyInfo> startBungeeProxy(BungeeGroup group, String name) {
        return null;
    }

    @Override
    public QueryRequest<BungeeCordProxyInfo> startBungeeProxy(BungeeGroup group, String name, int id, int memory) {
        return null;
    }

    @Override
    public QueryRequest<BungeeCordProxyInfo> startBungeeProxy(BungeeGroup group, String name, int memory) {
        return null;
    }

    @Override
    public QueryRequest<BungeeCordProxyInfo> startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group) {
        return null;
    }

    @Override
    public QueryRequest<BungeeCordProxyInfo> startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, int memory) {
        return null;
    }

    @Override
    public QueryRequest<BungeeCordProxyInfo> startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name) {
        return null;
    }

    @Override
    public QueryRequest<BungeeCordProxyInfo> startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name, int memory) {
        return null;
    }

    @Override
    public QueryRequest<BungeeCordProxyInfo> startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name, int id, int memory) {
        return null;
    }

    @Override
    public void startBungeeProxy(BungeeCordProxyInfo proxyInfo) {

    }

    @Override
    public void updateMinecraftGroup(MinecraftGroup group) {

    }

    @Override
    public void updateBungeeGroup(BungeeGroup group) {

    }

    @Override
    public void updateServerInfo(MinecraftServerInfo serverInfo) {

    }

    @Override
    public void updateProxyInfo(BungeeCordProxyInfo proxyInfo) {

    }

    @Override
    public QueryRequest<NodeInfo> getBestNodeInfo(int memoryNeeded) {
        return null;
    }

    @Override
    public QueryRequest<Collection<MinecraftServerInfo>> getMinecraftServers() {
        QueryRequest<Collection<MinecraftServerInfo>> request = new QueryRequest<>();
        this.packetManager.packetQueryAsync(this.nodeConnector, new PacketOutAPIQueryServerInfos()).onComplete(packet -> {
            if(packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if(simpleJsonObject != null && simpleJsonObject.contains("serverInfos"))
                    request.setResponse(Arrays.asList(simpleJsonObject.getObject("serverInfos", MinecraftServerInfo[].class)));
                else
                    request.setResponse(null);
            } else
                request.setResponse(null);
        });
        return request;
    }

    @Override
    public QueryRequest<Collection<MinecraftServerInfo>> getMinecraftServers(String group) {
        QueryRequest<Collection<MinecraftServerInfo>> request = new QueryRequest<>();
        this.packetManager.packetQueryAsync(this.nodeConnector, new PacketOutAPIQueryServerInfos(group)).onComplete(packet -> {
            if(packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if(simpleJsonObject != null && simpleJsonObject.contains("serverInfos"))
                    request.setResponse(Arrays.asList(simpleJsonObject.getObject("serverInfos", MinecraftServerInfo[].class)));
                else
                    request.setResponse(null);
            } else
                request.setResponse(null);
        });
        return request;
    }

    @Override
    public QueryRequest<Collection<MinecraftServerInfo>> getStartedMinecraftServers() {
        QueryRequest<Collection<MinecraftServerInfo>> request = new QueryRequest<>();
        this.packetManager.packetQueryAsync(this.nodeConnector, new PacketOutAPIQueryServerInfos(true)).onComplete(packet -> {
            if(packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if(simpleJsonObject != null && simpleJsonObject.contains("serverInfos"))
                    request.setResponse(Arrays.asList(simpleJsonObject.getObject("serverInfos", MinecraftServerInfo[].class)));
                else
                    request.setResponse(null);
            } else
                request.setResponse(null);
        });
        return request;
    }

    @Override
    public QueryRequest<Collection<MinecraftServerInfo>> getStartedMinecraftServers(String group) {
        QueryRequest<Collection<MinecraftServerInfo>> request = new QueryRequest<>();
        this.packetManager.packetQueryAsync(this.nodeConnector, new PacketOutAPIQueryServerInfos(group, true)).onComplete(packet -> {
            if(packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if(simpleJsonObject != null && simpleJsonObject.contains("serverInfos"))
                    request.setResponse(Arrays.asList(simpleJsonObject.getObject("serverInfos", MinecraftServerInfo[].class)));
                else
                    request.setResponse(null);
            } else
                request.setResponse(null);
        });
        return request;
    }

    @Override
    public QueryRequest<Collection<BungeeCordProxyInfo>> getBungeeProxies() {
        QueryRequest<Collection<BungeeCordProxyInfo>> request = new QueryRequest<>();
        this.packetManager.packetQueryAsync(this.nodeConnector, new PacketOutAPIQueryProxyInfos()).onComplete(packet -> {
            if(packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if(simpleJsonObject != null && simpleJsonObject.contains("proxyInfos"))
                    request.setResponse(Arrays.asList(simpleJsonObject.getObject("proxyInfos", BungeeCordProxyInfo[].class)));
                else
                    request.setResponse(null);
            } else
                request.setResponse(null);
        });
        return request;
    }

    @Override
    public QueryRequest<Collection<BungeeCordProxyInfo>> getBungeeProxies(String group) {
        QueryRequest<Collection<BungeeCordProxyInfo>> request = new QueryRequest<>();
        this.packetManager.packetQueryAsync(this.nodeConnector, new PacketOutAPIQueryProxyInfos(group)).onComplete(packet -> {
            if(packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if(simpleJsonObject != null && simpleJsonObject.contains("proxyInfos"))
                    request.setResponse(Arrays.asList(simpleJsonObject.getObject("proxyInfos", BungeeCordProxyInfo[].class)));
                else
                    request.setResponse(null);
            } else
                request.setResponse(null);
        });
        return request;
    }

    @Override
    public QueryRequest<Collection<BungeeCordProxyInfo>> getStartedBungeeProxies() {
        QueryRequest<Collection<BungeeCordProxyInfo>> request = new QueryRequest<>();
        this.packetManager.packetQueryAsync(this.nodeConnector, new PacketOutAPIQueryProxyInfos(true)).onComplete(packet -> {
            if(packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if(simpleJsonObject != null && simpleJsonObject.contains("proxyInfos"))
                    request.setResponse(Arrays.asList(simpleJsonObject.getObject("proxyInfos", BungeeCordProxyInfo[].class)));
                else
                    request.setResponse(null);
            } else
                request.setResponse(null);
        });
        return request;
    }

    @Override
    public QueryRequest<Collection<BungeeCordProxyInfo>> getStartedBungeeProxies(String group) {
        QueryRequest<Collection<BungeeCordProxyInfo>> request = new QueryRequest<>();
        this.packetManager.packetQueryAsync(this.nodeConnector, new PacketOutAPIQueryProxyInfos(group, true)).onComplete(packet -> {
            if(packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if(simpleJsonObject != null && simpleJsonObject.contains("proxyInfos"))
                    request.setResponse(Arrays.asList(simpleJsonObject.getObject("proxyInfos", BungeeCordProxyInfo[].class)));
                else
                    request.setResponse(null);
            } else
                request.setResponse(null);
        });
        return request;
    }

    @Override
    public Collection<MinecraftGroup> getMinecraftGroups() {
        if(this.minecraftGroups != null) {
            return this.minecraftGroups.values();
        } else {
            Packet packet = this.packetManager.packetQuery(this.nodeConnector, new PacketOutAPIQueryGroups(NetworkComponentType.MINECRAFT_SERVER));
            if (packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if (simpleJsonObject != null && simpleJsonObject.contains("groups")) {
                    Collection<MinecraftGroup> minecraftGroups = Arrays.asList(simpleJsonObject.getObject("groups", MinecraftGroup[].class));

                    this.minecraftGroups = new HashMap<>();
                    for(MinecraftGroup minecraftGroup : minecraftGroups)
                        this.minecraftGroups.put(minecraftGroup.getName().toLowerCase(), minecraftGroup);

                    return minecraftGroups;
                }
            }
        }
        return null;
    }

    @Override
    public Collection<BungeeGroup> getBungeeGroups() {
        if(this.bungeeGroups != null) {
            return this.bungeeGroups.values();
        } else {
            Packet packet = this.packetManager.packetQuery(this.nodeConnector, new PacketOutAPIQueryGroups(NetworkComponentType.BUNGEECORD));
            if (packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if (simpleJsonObject != null && simpleJsonObject.contains("groups")) {
                    Collection<BungeeGroup> bungeeGroups = Arrays.asList(simpleJsonObject.getObject("groups", BungeeGroup[].class));

                    this.bungeeGroups = new HashMap<>();
                    for(BungeeGroup bungeeGroup : bungeeGroups)
                        this.bungeeGroups.put(bungeeGroup.getName().toLowerCase(), bungeeGroup);

                    return bungeeGroups;
                }
            }
        }
        return null;
    }

    @Override
    public MinecraftGroup getMinecraftGroup(String name) {
        if(this.minecraftGroups == null)
            this.getMinecraftGroups();
        return this.minecraftGroups.get(name.toLowerCase());
    }

    @Override
    public BungeeGroup getBungeeGroup(String name) {
        if(this.bungeeGroups == null)
            this.getBungeeGroups();
        return this.bungeeGroups.get(name.toLowerCase());
    }

    @Override
    public QueryRequest<Collection<NodeInfo>> getNodeInfos() {
        return null;
    }

    @Override
    public QueryRequest<PeepoPlayer> getPlayer(UUID uniqueId) {
        return null;
    }

    @Override
    public QueryRequest<PeepoPlayer> getPlayer(String name) {
        return null;
    }

    @Override
    public QueryRequest<Map<UUID, PeepoPlayer>> getOnlinePlayers() {
        return null;
    }

    @Override
    public long getStartupTime() {
        return 0;
    }


    @Override
    public void sendPlayer(UUID uniqueId, String server) {

    }

    @Override
    public void sendPlayerActionBar(UUID uniqueId, BaseComponent... message) {

    }

    @Override
    public void sendPlayerMessage(UUID uniqueId, BaseComponent... components) {

    }

    @Override
    public void sendPlayerFallback(UUID uniqueId) {

    }

    @Override
    public void sendPlayerTitle(UUID uniqueId, BaseComponent[] title, BaseComponent[] subTitle, int fadeIn, int stay, int fadeOut) {

    }

    @Override
    public void playerChat(UUID uniqueId, String message) {

    }

    @Override
    public void kickPlayer(UUID uniqueId, BaseComponent... reason) {

    }

    @Override
    public void setPlayerTabHeaderFooter(UUID uniqueId, BaseComponent[] header, BaseComponent[] footer) {

    }

    @Override
    public void sendPlayerActionBar(UUID uniqueId, String message) {

    }

    @Override
    public void sendPlayerMessage(UUID uniqueId, String message) {

    }

    @Override
    public void sendPlayerTitle(UUID uniqueId, String title, String subTitle, int fadeIn, int stay, int fadeOut) {

    }

    @Override
    public void kickPlayer(UUID uniqueId, String reason) {

    }

    @Override
    public Collection<NetworkAPIHandler> getNetworkHandlers() {
        return networkHandlers;
    }

    @Override
    public NetworkClient getNodeConnector() {
        return nodeConnector;
    }

    @Override
    public PacketManager getPacketManager() {
        return packetManager;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    public static PeepoCloudPlugin getInstance() {
        return instance;
    }
}
