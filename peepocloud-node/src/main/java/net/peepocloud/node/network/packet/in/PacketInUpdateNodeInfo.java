package net.peepocloud.node.network.packet.in;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.network.node.NodeInfoUpdateEvent;
import net.peepocloud.node.network.ClientNodeImpl;
import net.peepocloud.node.network.participant.NodeParticipantImpl;
import net.peepocloud.node.utility.NodeUtils;

import java.util.function.Consumer;

public class PacketInUpdateNodeInfo extends JsonPacketHandler {
    @Override
    public int getId() {
        return 14;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        ClientNodeImpl node = PeepoCloudNode.getInstance().getConnectedNode(networkParticipant.getName());
        if (node != null) {
            NodeInfo nodeInfo = packet.getSimpleJsonObject().getObject("nodeInfo", NodeInfo.class);
            if (nodeInfo != null) {
                PeepoCloudNode.getInstance().getEventManager().callEvent(new NodeInfoUpdateEvent(node, nodeInfo, node.getNodeInfo()));
                if (node.getNodeInfo() == null || nodeInfo.getMaxMemory() != node.getNodeInfo().getMaxMemory()) {
                    NodeUtils.updateNodeInfoForSupport(null);
                }
                if (networkParticipant instanceof NodeParticipantImpl) {
                    ((NodeParticipantImpl) networkParticipant).setNodeInfo(nodeInfo);
                }
                node.setNodeInfo(nodeInfo);
            }
        }
    }
}
