package net.peepocloud.node.api.network;
/*
 * Created by Mc_Ruben on 05.01.2019
 */

import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public interface NetworkManager {

    /**
     * Sends the given {@link Packet} to all Nodes which are connected to this Node
     *
     * @param packet the packet to send
     */
    void sendPacketToNodes(Packet packet);

    /**
     * Sends the given {@link Packet} to all servers and proxies that are CONNECTED TO THIS NODE
     *
     * @param packet the packet to send
     */
   void sendPacketToServersAndProxiesOnThisNode(Packet packet);

    void sendPacketToServersOnThisNode(Packet packet);

    void sendPacketToProxiesOnThisNode(Packet packet);

    void sendPacketToServersAndProxies(Packet packet);

    void sendPacketToServers(Packet packet);

    void sendPacketToProxies(Packet packet);

    void sendPacketToServer(MinecraftServerInfo serverInfo, Packet packet);

    void sendPacketToProxy(BungeeCordProxyInfo proxyInfo, Packet packet);

    /**
     * Sends the given {@link Packet} to all servers, proxies and nodes that are CONNECTED TO THIS NODE
     *
     * @param packet the packet to send
     */
    void sendPacketToAllOnThisNode(Packet packet);

    /**
     * Sends the given {@link Packet} to all servers, proxies and nodes IN THE NETWORK
     *
     * @param packet the packet to send
     */
    void sendPacketToAll(Packet packet);

}
