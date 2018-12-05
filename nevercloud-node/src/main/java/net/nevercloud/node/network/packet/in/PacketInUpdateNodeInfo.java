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
import net.nevercloud.node.api.event.network.node.NodeInfoUpdateEvent;
import net.nevercloud.node.network.ClientNode;
import net.nevercloud.node.utility.NodeUtils;

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
        ClientNode node = NeverCloudNode.getInstance().getConnectedNode(networkParticipant.getName());
        if (node != null) {
            NodeInfo nodeInfo = ((PacketInUpdateNodeInfo) packet).getSimpleJsonObject().getObject("nodeInfo", NodeInfo.class);
            if (nodeInfo != null) {
                NeverCloudNode.getInstance().getEventManager().callEvent(new NodeInfoUpdateEvent(node, nodeInfo, node.getNodeInfo()));
                if (node.getNodeInfo() == null || nodeInfo.getMaxMemory() != node.getNodeInfo().getMaxMemory()) {
                    NodeUtils.updateNodeInfoForSupport(null);
                }
                node.setNodeInfo(nodeInfo);
            }
        }
    }
}
