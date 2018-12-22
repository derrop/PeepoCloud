package net.peepocloud.node.api;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import com.google.common.base.Preconditions;
import net.peepocloud.lib.network.packet.out.group.PacketOutCreateBungeeGroup;
import net.peepocloud.lib.network.packet.out.group.PacketOutCreateMinecraftGroup;
import net.peepocloud.lib.network.packet.out.server.PacketOutUpdateBungee;
import net.peepocloud.lib.network.packet.out.server.PacketOutUpdateServer;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.server.minecraft.MinecraftState;
import net.peepocloud.node.api.addon.AddonManager;
import net.peepocloud.node.api.addon.node.NodeAddon;
import net.peepocloud.node.api.command.CommandManager;
import net.peepocloud.node.api.command.CommandSender;
import net.peepocloud.node.api.database.DatabaseManager;
import net.peepocloud.node.api.event.EventManager;
import net.peepocloud.node.api.languagesystem.LanguagesManager;
import net.peepocloud.node.api.network.BungeeCordParticipant;
import net.peepocloud.node.api.network.ClientNode;
import net.peepocloud.node.api.network.MinecraftServerParticipant;
import net.peepocloud.node.api.network.NodeParticipant;
import net.peepocloud.node.api.server.CloudProcess;
import net.peepocloud.node.api.server.TemplateStorage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class PeepoCloudNodeAPI {

    private static PeepoCloudNodeAPI instance;

    public static void setInstance(PeepoCloudNodeAPI instance) {
        Preconditions.checkArgument(PeepoCloudNodeAPI.instance == null, "instance already set");
        PeepoCloudNodeAPI.instance = instance;
    }

    public static PeepoCloudNodeAPI getInstance() {
        return instance;
    }


    public abstract LanguagesManager getLanguagesManager();

    public abstract AddonManager<NodeAddon> getNodeAddonManager();

    public abstract DatabaseManager getDatabaseManager();

    public abstract CommandManager getCommandManager();

    public abstract EventManager getEventManager();

    public abstract Map<String, MinecraftServerParticipant> getServersOnThisNode();

    public abstract Map<String, BungeeCordParticipant> getProxiesOnThisNode();

    public abstract int getMemoryUsedOnThisInstanceByBungee();

    public abstract int getMemoryUsedOnThisInstanceByServer();

    public abstract long getStartupTime();

    public abstract Collection<TemplateStorage> getTemplateStorages();

    public abstract PeepoPlayer getPlayer(UUID uniqueId);

    public abstract PeepoPlayer getPlayer(String name);

    public abstract Map<UUID, PeepoPlayer> getOnlinePlayers();

    /**
     * Gets the {@link TemplateStorage} registered in this Node by the given {@code name}
     *
     * @param name the name of the {@link TemplateStorage}
     * @return the {@link TemplateStorage} or null if not found
     */
    public abstract TemplateStorage getTemplateStorage(String name);

    public abstract void updateMinecraftGroup(MinecraftGroup group);

    public abstract void updateBungeeGroup(BungeeGroup group);

    public abstract void updateServerInfo(MinecraftServerInfo serverInfo);

    public abstract void updateProxyInfo(BungeeCordProxyInfo proxyInfo);

    public abstract NodeInfo getBestNodeInfo(int memoryNeeded);

    public abstract Collection<MinecraftServerInfo> getMinecraftServers();

    public abstract Collection<MinecraftServerInfo> getMinecraftServers(String group);

    public abstract Collection<NodeInfo> getNodeInfos();

    public abstract Collection<MinecraftServerInfo> getStartedMinecraftServers();

    public abstract Collection<MinecraftServerInfo> getStartedMinecraftServers(String group);

    public abstract Collection<BungeeCordProxyInfo> getBungeeProxies();

    public abstract Collection<BungeeCordProxyInfo> getBungeeProxies(String group);

    public abstract Collection<BungeeCordProxyInfo> getStartedBungeeProxies();

    public abstract Collection<BungeeCordProxyInfo> getStartedBungeeProxies(String group);

    public abstract int getNextServerId(String group);

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
     * Copies a template by the loaded {@link TemplateStorage} specified in the {@link Template} or if not found the {@link TemplateLocalStorage} to the given {@link Path}
     *
     * @param group    the group of the server/proxy
     * @param template the {@link Template} to copy
     * @param target   the target where the files are copied in
     */
    public abstract void copyTemplate(String group, Template template, Path target);

}
