package net.nevercloud.node.network;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import lombok.*;
import net.nevercloud.lib.network.NetworkClient;
import net.nevercloud.lib.network.auth.Auth;
import net.nevercloud.lib.network.packet.PacketManager;
import net.nevercloud.lib.network.packet.handler.ChannelHandler;
import net.nevercloud.lib.node.NodeInfo;

import java.net.InetSocketAddress;

public class ClientNode extends NetworkClient {
    @Setter
    @Getter
    private NodeInfo nodeInfo;

    public ClientNode(InetSocketAddress address, PacketManager packetManager, ChannelHandler firstHandler, Auth auth, NodeInfo nodeInfo) {
        super(address, packetManager, firstHandler, auth);
        this.nodeInfo = nodeInfo;
    }
}