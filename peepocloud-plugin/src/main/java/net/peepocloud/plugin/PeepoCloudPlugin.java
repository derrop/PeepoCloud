package net.peepocloud.plugin;

import com.google.gson.reflect.TypeToken;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
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
import java.util.concurrent.TimeUnit;

public abstract class PeepoCloudPlugin extends PeepoCloudPluginAPI {
    private static PeepoCloudPlugin instance;

    protected PacketManager packetManager = new PacketManager();
    protected NetworkClient nodeConnector;
    protected Collection<NetworkAPIHandler> networkHandlers = new ArrayList<>();
    protected Scheduler scheduler = new Scheduler();

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
        this.nodeConnector.setConnectedHandler(this.handleConnected());

        // deleting the file because the info has been read
        try {
            Files.delete(nodeInfoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bootstrap() {
        scheduler.getThreadPool().execute(scheduler);

        this.packetManager.registerPacket(new PacketInAPIServerStarted());
        this.packetManager.registerPacket(new PacketInAPIServerStopped());
        this.packetManager.registerPacket(new PacketInAPIProxyStarted());
        this.packetManager.registerPacket(new PacketInAPIProxyStopped());

        scheduler.execute(this.nodeConnector, true);
    }

    @Override
    public void shutdown() {
        this.nodeConnector.shutdown();
        this.scheduler.disable();
    }

    public abstract Runnable handleConnected();

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
    public QueryRequest<Collection<MinecraftGroup>> getMinecraftGroups() {
        QueryRequest<Collection<MinecraftGroup>> request = new QueryRequest<>();
        this.packetManager.packetQueryAsync(this.nodeConnector, new PacketOutAPIQueryGroups(NetworkComponentType.MINECRAFT_SERVER)).onComplete(packet -> {
            if (packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if (simpleJsonObject != null && simpleJsonObject.contains("groups"))
                    request.setResponse(Arrays.asList(simpleJsonObject.getObject("groups", MinecraftGroup[].class)));
                else
                    request.setResponse(null);
            } else
                request.setResponse(null);
        });
        return request;
    }

    @Override
    public QueryRequest<Collection<BungeeGroup>> getBungeeGroups() {
        QueryRequest<Collection<BungeeGroup>> request = new QueryRequest<>();
        this.packetManager.packetQueryAsync(this.nodeConnector, new PacketOutAPIQueryGroups(NetworkComponentType.BUNGEECORD)).onComplete(packet -> {
            if (packet instanceof JsonPacket) {
                JsonPacket response = (JsonPacket) packet;
                SimpleJsonObject simpleJsonObject = response.getSimpleJsonObject();
                if (simpleJsonObject != null && simpleJsonObject.contains("groups"))
                    request.setResponse(Arrays.asList(simpleJsonObject.getObject("groups", BungeeGroup[].class)));
                else
                    request.setResponse(null);
            } else
                request.setResponse(null);
        });
        return request;
    }

    @Override
    public MinecraftGroup getMinecraftGroup(String name) {
        Collection<MinecraftGroup> minecraftGroups = this.getMinecraftGroups().complete();
        if(minecraftGroups != null)
            return minecraftGroups.stream().filter(minecraftGroup -> minecraftGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        return null;
    }

    @Override
    public BungeeGroup getBungeeGroup(String name) {
        Collection<BungeeGroup> bungeeGroups = this.getBungeeGroups().complete();
        if(bungeeGroups != null)
            return bungeeGroups.stream().filter(bungeeGroup -> bungeeGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        return null;
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
