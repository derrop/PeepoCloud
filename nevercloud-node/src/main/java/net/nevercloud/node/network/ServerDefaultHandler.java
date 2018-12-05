package net.nevercloud.node.network;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.event.network.node.NodeDisconnectEvent;
import net.nevercloud.node.network.participant.MinecraftServerParticipant;
import net.nevercloud.node.network.participant.NodeParticipant;
import net.nevercloud.node.utility.NodeUtils;

public class ServerDefaultHandler extends ChannelHandlerAdapter {
    @Override
    public void disconnected(NetworkParticipant networkParticipant) {
        if (networkParticipant instanceof NodeParticipant) {
            NeverCloudNode.getInstance().getEventManager().callEvent(new NodeDisconnectEvent((NodeParticipant) networkParticipant));
            NeverCloudNode.getInstance().getScreenManager().getNetworkScreenManager().handleNodeDisconnect((NodeParticipant) networkParticipant);
            NeverCloudNode.getInstance().getServerNodes().remove(networkParticipant.getName());
            NodeUtils.updateNodeInfoForSupport(null);
        } else if (networkParticipant instanceof MinecraftServerParticipant) {
            //NeverCloudNode.getInstance().getEventManager().callEvent()
        }
    }

    @Override
    public void connected(NetworkParticipant networkParticipant) {
        if (networkParticipant instanceof NodeParticipant) {
            NeverCloudNode.getInstance().getServerNodes().put(networkParticipant.getName(), (NodeParticipant) networkParticipant);
        }
    }
}
