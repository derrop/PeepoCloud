package net.peepocloud.api;
/*
 * Created by Mc_Ruben on 16.12.2018
 */

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.peepocloud.api.node.NodeInfo;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.api.server.bungee.BungeeGroup;
import net.peepocloud.api.server.minecraft.MinecraftGroup;
import net.peepocloud.api.server.minecraft.MinecraftServerInfo;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PeepoAPI {

    @Getter
    private static PeepoAPI instance;

    public static void setInstance(PeepoAPI instance) {
        Preconditions.checkArgument(PeepoAPI.instance == null, "instance already set");
        PeepoAPI.instance = instance;
    }

    public boolean isNode() {
        return false;
    }

    public boolean isBungee() {
        return false;
    }

    public boolean isSpigot() {
        return false;
    }

    public boolean isSponge() {
        return false;
    }

    public abstract MinecraftGroup getMinecraftGroup(String name);

    public abstract BungeeGroup getBungeeGroup(String name);

    public abstract BungeeCordProxyInfo startBungeeProxy(BungeeGroup group);

    public abstract BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, int memory);

    public abstract BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, String name);

    public abstract BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, String name, int id, int memory);

    public abstract BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, String name, int memory);

    public abstract BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group);

    public abstract BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, int memory);

    public abstract BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name);

    public abstract BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name, int memory);

    public abstract BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name, int id, int memory);

    public abstract void startBungeeProxy(BungeeCordProxyInfo proxyInfo);


    public abstract MinecraftServerInfo startMinecraftServer(MinecraftGroup group);

    public abstract MinecraftServerInfo startMinecraftServer(MinecraftGroup group, int memory);

    public abstract MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name);

    public abstract MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name, int id, int memory);

    public abstract MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name, int memory);

    public abstract MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group);

    public abstract MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, int memory);

    public abstract MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name);

    public abstract MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int memory);

    public abstract MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int id, int memory);

    public abstract void startMinecraftServer(MinecraftServerInfo serverInfo);

    public abstract void stopBungeeProxy(String name);

    public abstract void stopBungeeProxy(BungeeCordProxyInfo proxyInfo);

    public abstract void stopMinecraftServer(String name);

    public abstract void stopMinecraftServer(MinecraftServerInfo serverInfo);

    /**
     * Gets all {@link BungeeCordProxyInfo}'s which are from the group specified in the parameters of the starting proxies in the network (on all nodes)
     *
     * @param group the name of the group
     * @return an {@link ArrayList} with all infos of the starting proxies in the network from the group {@code group}
     */
    public abstract Collection<BungeeCordProxyInfo> getStartedBungeeProxies(String group);

    /**
     * Gets all {@link BungeeCordProxyInfo}'s of the starting proxies in the network (on all nodes)
     *
     * @return an {@link ArrayList} with all infos of the starting proxies in the network
     */
    public abstract Collection<BungeeCordProxyInfo> getStartedBungeeProxies();

    /**
     * Gets all {@link BungeeCordProxyInfo}'s which are from the group specified in the parameters of the running and starting proxies in the network (on all nodes)
     *
     * @param group the name of the group
     * @return an {@link ArrayList} with all infos of the servers in the network from the group {@code group}
     */
    public abstract Collection<BungeeCordProxyInfo> getBungeeProxies(String group);

    /**
     * Gets all {@link BungeeCordProxyInfo}'s of the running and starting proxies in the network (on all nodes)
     *
     * @return an {@link ArrayList} with all infos of the proxies in the network
     */
    public abstract Collection<BungeeCordProxyInfo> getBungeeProxies();

    /**
     * Gets all {@link MinecraftServerInfo}'s of the starting servers in the network (on all nodes)
     *
     * @return an {@link ArrayList} with all infos of the starting servers in the network
     */
    public abstract Collection<MinecraftServerInfo> getStartedMinecraftServers();

    /**
     * Gets all {@link MinecraftServerInfo}'s which are from the group specified in the parameters of the starting servers in the network (on all nodes)
     *
     * @param group the name of the group
     * @return an {@link ArrayList} with all infos of the starting servers in the network from the group {@code group}
     */
    public abstract Collection<MinecraftServerInfo> getStartedMinecraftServers(String group);

    /**
     * Gets all {@link MinecraftServerInfo}'s of the running and starting servers in the network (on all nodes)
     *
     * @return an {@link ArrayList} with all infos of the servers in the network
     */
    public abstract Collection<MinecraftServerInfo> getMinecraftServers();

    /**
     * Gets all {@link MinecraftServerInfo}'s which are from the group specified in the parameters of the running and starting servers in the network (on all nodes)
     *
     * @param group the name of the group
     * @return an {@link ArrayList} with all infos of the servers in the network from the group {@code group}
     */
    public abstract Collection<MinecraftServerInfo> getMinecraftServers(String group);

    /**
     * Gets the best {@link NodeInfo} of a node in the network sorted by their memory usage, it must have the given {@code memoryNeeded} memory free
     * @param memoryNeeded the memory the node needs that it can be the best
     * @return the best {@link NodeInfo} in the network sorted by the memory which has {@code memoryNeeded} free memory, or null if no node in the network has the {@code memoryNeeded} free
     */
    public abstract NodeInfo getBestNodeInfo(int memoryNeeded);

    public abstract void updateProxyInfo(BungeeCordProxyInfo proxyInfo);

    public abstract void updateServerInfo(MinecraftServerInfo serverInfo);

    public abstract void updateBungeeGroup(BungeeGroup group);

    public abstract void updateMinecraftGroup(MinecraftGroup group);

    /**
     * Gets the memory used by all servers/proxies in the network
     *
     * @return the memory of all servers/proxies in the network
     */
    public abstract int getMemoryUsed();

    /**
     * Gets the max memory of all nodes in the network
     *
     * @return the memory of all nodes in the network
     */
    public abstract int getMaxMemory();

}
