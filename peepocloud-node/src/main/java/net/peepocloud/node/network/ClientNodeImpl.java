package net.peepocloud.node.network;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import lombok.*;
import net.peepocloud.lib.network.NetworkClient;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.network.packet.handler.ChannelHandler;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.node.api.network.ClientNode;

import java.net.InetSocketAddress;

public class ClientNodeImpl extends NetworkClient implements ClientNode {
    @Setter
    @Getter
    private NodeInfo nodeInfo;

    public ClientNodeImpl(InetSocketAddress address, PacketManager packetManager, ChannelHandler firstHandler, Auth auth, NodeInfo nodeInfo) {
        super(address, packetManager, firstHandler, auth);
        this.nodeInfo = nodeInfo;
    }
}