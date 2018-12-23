package net.peepocloud.node.api;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import com.google.common.base.Preconditions;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.api.addon.AddonManager;
import net.peepocloud.node.api.addon.node.NodeAddon;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandManager;
import net.peepocloud.node.api.database.DatabaseAddon;
import net.peepocloud.node.api.database.DatabaseManager;
import net.peepocloud.node.api.event.Event;
import net.peepocloud.node.api.event.EventManager;
import net.peepocloud.node.api.languagesystem.Language;
import net.peepocloud.node.api.languagesystem.LanguagesManager;
import net.peepocloud.node.api.network.BungeeCordParticipant;
import net.peepocloud.node.api.network.ClientNode;
import net.peepocloud.node.api.network.MinecraftServerParticipant;
import net.peepocloud.node.api.network.NodeParticipant;
import net.peepocloud.node.api.server.TemplateStorage;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public abstract class PeepoCloudNodeAPI {

    private static PeepoCloudNodeAPI instance;

    public static void setInstance(PeepoCloudNodeAPI instance) {
        Preconditions.checkArgument(PeepoCloudNodeAPI.instance == null, "instance already set");
        PeepoCloudNodeAPI.instance = instance;
    }

    public static PeepoCloudNodeAPI getInstance() {
        return instance;
    }

    /**
     * Gets the {@link LanguagesManager} instance of this Node
     *
     * @return the {@link LanguagesManager} of this Node containing all messages for the selected {@link Language}
     */
    public abstract LanguagesManager getLanguagesManager();

    /**
     * Gets the {@link AddonManager} loading all the {@link NodeAddon}s for this Node
     *
     * @return the {@link AddonManager} of this Node
     */
    public abstract AddonManager<NodeAddon> getNodeAddonManager();

    /**
     * Gets the {@link DatabaseManager} of this Node which can be loaded directly in the Node or by {@link DatabaseAddon}s
     *
     * @return the {@link DatabaseManager} instance for this Node
     */
    public abstract DatabaseManager getDatabaseManager();

    /**
     * Gets the {@link CommandManager} for this Node instance
     *
     * @return the {@link CommandManager} of this Node containing all {@link Command}s
     */
    public abstract CommandManager getCommandManager();

    /**
     * Gets the {@link EventManager} for this Node instance
     *
     * @return the {@link EventManager} of this Node containing all registered Listeners which are called every time an {@link Event} is fired
     */
    public abstract EventManager getEventManager();

    /**
     * Gets the {@link PacketManager} instance for this Node
     *
     * @return the {@link PacketManager} instance of this Node containing all registered {@link Packet}s
     */
    public abstract PacketManager getPacketManager();

    /**
     * Gets all servers which are connected to this Node
     *
     * @return a Map with all servers which are connected to this Node by their Names as Keys
     */
    public abstract Map<String, MinecraftServerParticipant> getServersOnThisNode();

    /**
     * Gets all proxies which are connected to this Node
     *
     * @return a Map with all proxies which are connected to this Node by their Names as Keys
     */
    public abstract Map<String, BungeeCordParticipant> getProxiesOnThisNode();

    /**
     * Gets the max memory of all proxies on this Node instance
     *
     * @return the max memory of all proxies on this Node
     */
    public abstract int getMemoryUsedOnThisInstanceByBungee();

    /**
     * Gets the max memory of all servers on this Node instance
     *
     * @return the max memory of all servers on this Node
     */
    public abstract int getMemoryUsedOnThisInstanceByServer();

    /**
     * Gets the time this Node has been started in milliseconds
     *
     * @return the time when this Node was started
     */
    public abstract long getStartupTime();

    /**
     * Gets all {@link TemplateStorage}s registered in this Node
     *
     * @return a {@link Collection} containing all {@link TemplateStorage}s in this Node
     */
    public abstract Collection<TemplateStorage> getTemplateStorages();

    /**
     * Gets an online player by their UniqueId
     *
     * @param uniqueId the uniqueId of the player
     * @return the OnlinePlayer if it is online or {@code null} if no player with this {@code uniqueId} was found
     */
    public abstract PeepoPlayer getPlayer(UUID uniqueId);

    /**
     * Gets an online player by their Name
     *
     * @param name the name of the player
     * @return the OnlinePlayer if it is online or {@code null} if no player with this {@code name} was found
     */
    public abstract PeepoPlayer getPlayer(String name);

    /**
     * Gets all online players connected to the network
     *
     * @return all players by their UniqueId
     */
    public abstract Map<UUID, PeepoPlayer> getOnlinePlayers();

    /**
     * Gets the {@link TemplateStorage} registered in this Node by the given {@code name}
     *
     * @param name the name of the {@link TemplateStorage}
     * @return the {@link TemplateStorage} or null if not found
     */
    public abstract TemplateStorage getTemplateStorage(String name);

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
    public abstract NodeInfo getBestNodeInfo(int memoryNeeded);

    /**
     * Gets the infos of all running/starting minecraft servers in the network
     *
     * @return the infos of all running/starting minecraft servers in the network
     */
    public abstract Collection<MinecraftServerInfo> getMinecraftServers();

    /**
     * Gets the infos of all running/starting minecraft servers in the network by the given {@code group}
     *
     * @param group the group the servers must have
     * @return the infos of all running/starting minecraft servers in the network which are from the group {@code group}
     */
    public abstract Collection<MinecraftServerInfo> getMinecraftServers(String group);

    /**
     * Gets the infos of all running minecraft servers in the network
     *
     * @return the infos of all running minecraft servers in the network
     */
    public abstract Collection<MinecraftServerInfo> getStartedMinecraftServers();

    /**
     * Gets the infos of all running minecraft servers in the network by the given {@code group}
     *
     * @param group the group the servers must have
     * @return the infos of all running minecraft servers in the network which are from the group {@code group}
     */
    public abstract Collection<MinecraftServerInfo> getStartedMinecraftServers(String group);

    /**
     * Gets the infos of all running/starting proxies in the network
     *
     * @return the infos of all running/starting proxies in the network
     */
    public abstract Collection<BungeeCordProxyInfo> getBungeeProxies();

    /**
     * Gets the infos of all running/starting proxies in the network by the given {@code group}
     *
     * @param group the group the servers must have
     * @return the infos of all running/starting proxies in the network which are from the group {@code group}
     */
    public abstract Collection<BungeeCordProxyInfo> getBungeeProxies(String group);

    /**
     * Gets the infos of all running proxies in the network
     *
     * @return the infos of all running proxies in the network
     */
    public abstract Collection<BungeeCordProxyInfo> getStartedBungeeProxies();

    /**
     * Gets the infos of all running proxies in the network by the given {@code group}
     *
     * @param group the group the servers must have
     * @return the infos of all running proxies in the network which are from the group {@code group}
     */
    public abstract Collection<BungeeCordProxyInfo> getStartedBungeeProxies(String group);

    /**
     * Gets the infos of all nodes connected in the network
     *
     * @return the {@link NodeInfo}s of all connected nodes
     */
    public abstract Collection<NodeInfo> getNodeInfos();

    /**
     * Gets the next id for a server of the given {@code group}
     *
     * @param group the group to get the next id from
     * @return the next id for the servers of the given {@code group}
     */
    public abstract int getNextServerId(String group);

    /**
     * Gets the next id for a proxy of the given {@code group}
     *
     * @param group the group to get the next id from
     * @return the next id for the proxies of the given {@code group}
     */
    public abstract int getNextProxyId(String group);

    public abstract boolean isServerStarted(String name);

    public abstract boolean isProxyStarted(String name);

    public abstract MinecraftServerInfo getMinecraftServerInfo(String name);

    public abstract BungeeCordProxyInfo getBungeeProxyInfo(String name);

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

    public abstract void stopBungeeGroup(String name);

    public abstract void stopMinecraftGroup(String name);

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

    public abstract Template findTemplate(MinecraftGroup group);

    public abstract Template findTemplate(BungeeGroup group);

    /**
     * Gets the uniqueId of the network (used for example for the support)
     *
     * @return the uniqueId
     */
    public abstract String getUniqueId();

    /**
     * Gets all ports bound by the servers/proxies of this Node
     *
     * @return a collection with all bound ports
     */
    public abstract Collection<Integer> getBoundPorts();

    /**
     * Shuts down the system
     */
    public abstract void shutdown();

    /**
     * Reloads all the configs, addons, etc. of the system
     */
    public abstract void reload();

    /**
     * Reloads all configs of the system
     */
    public abstract void reloadConfigs();

    /**
     * Reloads all addons of the system
     */
    public abstract void reloadAddons();

    /**
     * Gets the local address of the server
     *
     * @return the local address or if it could not be detected "could not detect local address"
     */
    public abstract String getLocalAddress();

    /**
     * The amount of memory used of all the servers and proxies on this node instance
     *
     * @return the memory used on this instance in MB
     */
    public abstract int getMemoryUsedOnThisInstance();

    /**
     * Gets the nodes connected as a client to this node as a server
     *
     * @return the nodes connected to this node
     */
    public abstract Map<String, NodeParticipant> getServerNodes();

    /**
     * Gets a node connected to this node as a client
     *
     * @param name the name of the node
     * @return the connected node or null, if no node with the given {@code name} is connected
     */
    public abstract ClientNode getConnectedNode(String name);


    public abstract BungeeGroup getBungeeGroup(String name);

    public abstract MinecraftGroup getMinecraftGroup(String name);

    /**
     * Copies a template by the loaded {@link TemplateStorage} specified in the {@link Template} or if not found the local storage to the given {@link Path}
     *
     * @param group    the group of the server/proxy
     * @param template the {@link Template} to copy
     * @param target   the target where the files are copied in
     */
    public abstract void copyTemplate(String group, Template template, Path target);

}
