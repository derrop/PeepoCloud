package net.peepocloud.plugin.api;


import com.google.common.base.Preconditions;
import net.peepocloud.lib.AbstractPeepoCloudAPI;
import net.peepocloud.lib.network.NetworkClient;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.lib.scheduler.Scheduler;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.utility.network.QueryRequest;
import net.peepocloud.plugin.api.bukkit.PeepoCloudBukkitAPI;
import net.peepocloud.plugin.api.bungee.PeepoCloudBungeeAPI;
import net.peepocloud.plugin.api.network.handler.NetworkAPIHandler;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public abstract class PeepoCloudPluginAPI extends AbstractPeepoCloudAPI {
    private static PeepoCloudPluginAPI instance;

    public static void setInstance(PeepoCloudPluginAPI instance) {
        Preconditions.checkArgument(PeepoCloudPluginAPI.instance == null, "Instance already set");
        PeepoCloudPluginAPI.instance = instance;
        AbstractPeepoCloudAPI.setInstance(instance);
    }

    public static PeepoCloudPluginAPI getInstance() {
        return instance;
    }

    public abstract void bootstrap();
    public abstract void shutdown();

    public abstract void handleSuccessfulLogin();

    public abstract boolean isBukkit();
    public abstract boolean isBungee();
    public abstract PeepoCloudBukkitAPI toBukkit();
    public abstract PeepoCloudBungeeAPI toBungee();

    public abstract void registerNetworkHandler(NetworkAPIHandler handler);
    public abstract boolean unregisterNetworkHandler(NetworkAPIHandler handler);

    public abstract boolean isServerStarted(String name);

    public abstract boolean isProxyStarted(String name);

    public abstract QueryRequest<MinecraftServerInfo> getMinecraftServerInfo(String name);

    public abstract QueryRequest<BungeeCordProxyInfo> getBungeeProxyInfo(String name);

    public abstract QueryRequest<MinecraftServerInfo> startMinecraftServer(MinecraftGroup group);

    public abstract QueryRequest<MinecraftServerInfo> startMinecraftServer(MinecraftGroup group, int memory);

    public abstract QueryRequest<MinecraftServerInfo> startMinecraftServer(MinecraftGroup group, String name);

    public abstract QueryRequest<MinecraftServerInfo> startMinecraftServer(MinecraftGroup group, String name, int id, int memory);

    public abstract QueryRequest<MinecraftServerInfo> startMinecraftServer(MinecraftGroup group, String name, int memory);

    public abstract QueryRequest<MinecraftServerInfo> startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group);

    public abstract QueryRequest<MinecraftServerInfo> startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, int memory);

    public abstract QueryRequest<MinecraftServerInfo> startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name);

    public abstract QueryRequest<MinecraftServerInfo> startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int memory);

    public abstract QueryRequest<MinecraftServerInfo> startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int id, int memory);

    public abstract void startMinecraftServer(MinecraftServerInfo serverInfo);

    public abstract void stopBungeeProxy(String name);

    public abstract void stopBungeeProxy(BungeeCordProxyInfo proxyInfo);

    public abstract void stopMinecraftServer(String name);

    public abstract void stopMinecraftServer(MinecraftServerInfo serverInfo);

    public abstract void stopBungeeGroup(String name);

    public abstract void stopMinecraftGroup(String name);

    public abstract QueryRequest<BungeeCordProxyInfo> startBungeeProxy(BungeeGroup group);

    public abstract QueryRequest<BungeeCordProxyInfo> startBungeeProxy(BungeeGroup group, int memory);

    public abstract QueryRequest<BungeeCordProxyInfo> startBungeeProxy(BungeeGroup group, String name);

    public abstract QueryRequest<BungeeCordProxyInfo> startBungeeProxy(BungeeGroup group, String name, int id, int memory);

    public abstract QueryRequest<BungeeCordProxyInfo> startBungeeProxy(BungeeGroup group, String name, int memory);

    public abstract QueryRequest<BungeeCordProxyInfo> startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group);

    public abstract QueryRequest<BungeeCordProxyInfo> startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, int memory);

    public abstract QueryRequest<BungeeCordProxyInfo> startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name);

    public abstract QueryRequest<BungeeCordProxyInfo> startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name, int memory);

    public abstract QueryRequest<BungeeCordProxyInfo> startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name, int id, int memory);

    public abstract void startBungeeProxy(BungeeCordProxyInfo proxyInfo);

    /**
     * Updates a {@link MinecraftGroup} to the database
     *
     * @param group the group to update
     */
    public abstract void updateMinecraftGroup(MinecraftGroup group);

    /**
     * Updates a {@link BungeeGroup} to the database
     *
     * @param group the group to update
     */
    public abstract void updateBungeeGroup(BungeeGroup group);

    /**
     * Updates a {@link MinecraftServerInfo} to the network
     *
     * @param serverInfo the serverInfo to update
     */
    public abstract void updateServerInfo(MinecraftServerInfo serverInfo);

    /**
     * Updates a {@link BungeeCordProxyInfo} to the network
     *
     * @param proxyInfo the proxyInfo to update
     */
    public abstract void updateProxyInfo(BungeeCordProxyInfo proxyInfo);

    /**
     * Gets the best {@link NodeInfo} connected in the network which has the given {@code memoryNeeded} memory free
     *
     * @param memoryNeeded the memory the node must have
     * @return the info of the best Node which has the given {@code memoryNeeded} free memory
     */
    public abstract QueryRequest<NodeInfo> getBestNodeInfo(int memoryNeeded);

    /**
     * Gets the infos of all running/starting minecraft servers in the network
     *
     * @return the infos of all running/starting minecraft servers in the network
     */
    public abstract QueryRequest<Collection<MinecraftServerInfo>> getMinecraftServers();

    /**
     * Gets the infos of all running/starting minecraft servers in the network by the given {@code group}
     *
     * @param group the group the servers must have
     * @return the infos of all running/starting minecraft servers in the network which are from the group {@code group}
     */
    public abstract QueryRequest<Collection<MinecraftServerInfo>> getMinecraftServers(String group);

    /**
     * Gets the infos of all running minecraft servers in the network
     *
     * @return the infos of all running minecraft servers in the network
     */
    public abstract QueryRequest<Collection<MinecraftServerInfo>> getStartedMinecraftServers();

    /**
     * Gets the infos of all running minecraft servers in the network by the given {@code group}
     *
     * @param group the group the servers must have
     * @return the infos of all running minecraft servers in the network which are from the group {@code group}
     */
    public abstract QueryRequest<Collection<MinecraftServerInfo>> getStartedMinecraftServers(String group);

    /**
     * Gets the infos of all running/starting proxies in the network
     *
     * @return the infos of all running/starting proxies in the network
     */
    public abstract QueryRequest<Collection<BungeeCordProxyInfo>> getBungeeProxies();

    /**
     * Gets the infos of all running/starting proxies in the network by the given {@code group}
     *
     * @param group the group the servers must have
     * @return the infos of all running/starting proxies in the network which are from the group {@code group}
     */
    public abstract QueryRequest<Collection<BungeeCordProxyInfo>> getBungeeProxies(String group);

    /**
     * Gets the infos of all running proxies in the network
     *
     * @return the infos of all running proxies in the network
     */
    public abstract QueryRequest<Collection<BungeeCordProxyInfo>> getStartedBungeeProxies();

    /**
     * Gets the infos of all running proxies in the network by the given {@code group}
     *
     * @param group the group the servers must have
     * @return the infos of all running proxies in the network which are from the group {@code group}
     */
    public abstract QueryRequest<Collection<BungeeCordProxyInfo>> getStartedBungeeProxies(String group);


    public abstract Collection<MinecraftGroup> getMinecraftGroups();

    public abstract Collection<BungeeGroup> getBungeeGroups();


    /**
     * Gets the infos of all nodes connected in the network
     *
     * @return the {@link NodeInfo}s of all connected nodes
     */
    public abstract QueryRequest<Collection<NodeInfo>> getNodeInfos();

    /**
     * Gets an online player by their UniqueId
     *
     * @param uniqueId the uniqueId of the player
     * @return the OnlinePlayer if it is online or {@code null} if no player with this {@code uniqueId} was found
     */
    public abstract QueryRequest<PeepoPlayer> getPlayer(UUID uniqueId);

    /**
     * Gets an online player by their Name
     *
     * @param name the name of the player
     * @return the OnlinePlayer if it is online or {@code null} if no player with this {@code name} was found
     */
    public abstract QueryRequest<PeepoPlayer> getPlayer(String name);

    /**
     * Gets all online players connected to the network
     *
     * @return all players by their UniqueId
     */
    public abstract QueryRequest<Map<UUID, PeepoPlayer>> getOnlinePlayers();

    /**
     * Gets the time the Node on which this server/proxy has been started in milliseconds
     *
     * @return the time when this Node was started
     */
    public abstract long getStartupTime();

    public abstract PacketManager getPacketManager();
    public abstract NetworkClient getNodeConnector();
    public abstract Collection<NetworkAPIHandler> getNetworkHandlers();
    public abstract Scheduler getScheduler();



}
