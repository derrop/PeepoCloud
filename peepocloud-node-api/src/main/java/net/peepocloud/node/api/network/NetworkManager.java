package net.peepocloud.node.api.network;
/*
 * Created by Mc_Ruben on 05.01.2019
 */

import net.peepocloud.lib.network.packet.Packet;

public interface NetworkManager {

    /**
     * Sends the given {@link Packet} to all Nodes which are connected to this Node
     *
     * @param packet the packet to send
     */
    public void sendPacketToNodes(Packet packet);

    /**
     * Sends the given {@link Packet} to all servers and proxies that are CONNECTED TO THIS NODE
     *
     * @param packet the packet to send
     */
    public void sendPacketToServersAndProxiesOnThisNode(Packet packet);

    public void sendPacketToServersOnThisNode(Packet packet);

    public void sendPacketToProxiesOnThisNode(Packet packet);

    public void sendPacketToServersAndProxies(Packet packet);

    public void sendPacketToServers(Packet packet);

    public void sendPacketToProxies(Packet packet);

    /**
     * Sends the given {@link Packet} to all servers, proxies and nodes that are CONNECTED TO THIS NODE
     *
     * @param packet the packet to send
     */
    public void sendPacketToAllOnThisNode(Packet packet);

    /**
     * Sends the given {@link Packet} to all servers, proxies and nodes IN THE NETWORK
     *
     * @param packet the packet to send
     */
    public void sendPacketToAll(Packet packet);

}
