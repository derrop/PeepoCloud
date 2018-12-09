package net.peepocloud.node.network.packet.in;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.network.node.NodeInfoUpdateEvent;
import net.peepocloud.node.network.ClientNode;
import net.peepocloud.node.utility.NodeUtils;

import java.util.function.Consumer;

public class PacketInUpdateNodeInfo extends JsonPacket implements PacketHandler {
    public PacketInUpdateNodeInfo(int id) {
        super(id);
    }

    public PacketInUpdateNodeInfo() {
        super(14);
    }

    @Override
    public void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        if (!(packet instanceof PacketInUpdateNodeInfo))
            return;
        ClientNode node = PeepoCloudNode.getInstance().getConnectedNode(networkParticipant.getName());
        if (node != null) {
            NodeInfo nodeInfo = ((PacketInUpdateNodeInfo) packet).getSimpleJsonObject().getObject("nodeInfo", NodeInfo.class);
            if (nodeInfo != null) {
                PeepoCloudNode.getInstance().getEventManager().callEvent(new NodeInfoUpdateEvent(node, nodeInfo, node.getNodeInfo()));
                if (node.getNodeInfo() == null || nodeInfo.getMaxMemory() != node.getNodeInfo().getMaxMemory()) {
                    NodeUtils.updateNodeInfoForSupport(null);
                }
                node.setNodeInfo(nodeInfo);
            }
        }
    }
}
