package net.nevercloud.node.network.packet.in;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.handler.PacketHandler;
import net.nevercloud.lib.node.NodeInfo;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.network.ClientNode;

import java.util.function.Consumer;

public class PacketCInUpdateNodeInfo extends JsonPacket implements PacketHandler {
    public PacketCInUpdateNodeInfo(int id) {
        super(id);
    }

    public PacketCInUpdateNodeInfo() {
        super(14);
    }

    @Override
    public void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        if (!(packet instanceof PacketCInUpdateNodeInfo))
            return;
        ClientNode node = NeverCloudNode.getInstance().getConnectedNode(networkParticipant.getName());
        if (node != null) {
            NodeInfo nodeInfo = ((PacketCInUpdateNodeInfo) packet).getSimpleJsonObject().getObject("nodeInfo", NodeInfo.class);
            if (nodeInfo != null) {
                node.setNodeInfo(nodeInfo);
            }
        }
    }
}
