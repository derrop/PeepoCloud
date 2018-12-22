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

    Auth getAuth();

    Map<String, MinecraftServerInfo> getServers();

    Map<String, MinecraftServerInfo> getWaitingServers();

    Map<String, MinecraftServerInfo> getStartingServers();

    Map<String, BungeeCordProxyInfo> getProxies();

    Map<String, BungeeCordProxyInfo> getWaitingProxies();

    Map<String, BungeeCordProxyInfo> getStartingProxies();

    NodeInfo getNodeInfo();

    void startMinecraftServer(MinecraftServerInfo serverInfo);

    void startBungeeCordProxy(BungeeCordProxyInfo proxyInfo);

    void closeConnection();

}
