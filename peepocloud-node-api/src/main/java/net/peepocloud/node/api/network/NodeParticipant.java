package net.peepocloud.node.api.network;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

import java.util.Map;

public interface NodeParticipant extends NetworkPacketSender {

    /**
     * Gets the {@link Auth} which was used for this participant to authenticate
     *
     * @return the {@link Auth} of this participant
     */
    Auth getAuth();

    /**
     * Gets all servers that running on this Node
     *
     * @return a Map with all infos of the servers that are running on this Node by their names
     */
    Map<String, MinecraftServerInfo> getServers();

    /**
     * Gets all servers that are in the queue of this Node
     *
     * @return a Map with all infos of the servers that are in the queue of this Node by their names
     */
    Map<String, MinecraftServerInfo> getWaitingServers();

    /**
     * Gets all servers that are currently starting on this Node
     *
     * @return a Map with all infos of the servers that are currently starting on this Node by their names
     */
    Map<String, MinecraftServerInfo> getStartingServers();

    /**
     * Gets all proxies that running on this Node
     *
     * @return a Map with all infos of the proxies that are running on this Node by their names
     */
    Map<String, BungeeCordProxyInfo> getProxies();

    /**
     * Gets all proxies that are in the queue of this Node
     *
     * @return a Map with all infos of the proxies that are in the queue of this Node by their names
     */
    Map<String, BungeeCordProxyInfo> getWaitingProxies();

    /**
     * Gets all proxies that are currently starting on this Node
     *
     * @return a Map with all infos of the proxies that are currently starting on this Node by their names
     */
    Map<String, BungeeCordProxyInfo> getStartingProxies();

    /**
     * Gets the current info of this Node
     *
     * @return the info of this Node
     */
    NodeInfo getNodeInfo();

    /**
     * Starts a new server by the given {@code serverInfo} on this Node
     *
     * @param serverInfo the info for the server to start
     */
    void startMinecraftServer(MinecraftServerInfo serverInfo);

    /**
     * Starts a new proxy by the given {@code proxyInfo} on this Node
     *
     * @param proxyInfo the info for the proxy to start
     */
    void startBungeeCordProxy(BungeeCordProxyInfo proxyInfo);

    /**
     * Closes the connection to this node and clears all servers/proxies that are registered with this Node
     */
    void closeConnection();

    /**
     * Executes the given {@code command} to this Node
     *
     * @param command the command to execute
     */
    void executeCommand(String command);

}
