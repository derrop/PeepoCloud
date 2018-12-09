package net.peepocloud.node.network;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.network.node.NodeDisconnectEvent;
import net.peepocloud.node.network.participant.MinecraftServerParticipant;
import net.peepocloud.node.network.participant.NodeParticipant;
import net.peepocloud.node.utility.NodeUtils;

public class ServerDefaultHandler extends ChannelHandlerAdapter {
    @Override
    public void disconnected(NetworkParticipant networkParticipant) {
        if (networkParticipant instanceof NodeParticipant) {
            PeepoCloudNode.getInstance().getEventManager().callEvent(new NodeDisconnectEvent((NodeParticipant) networkParticipant));
            PeepoCloudNode.getInstance().getScreenManager().getNetworkScreenManager().handleNodeDisconnect((NodeParticipant) networkParticipant);
            PeepoCloudNode.getInstance().getServerNodes().remove(networkParticipant.getName());
            NodeUtils.updateNodeInfoForSupport(null);
        } else if (networkParticipant instanceof MinecraftServerParticipant) {
            //PeepoCloudNode.getInstance().getEventManager().callEvent()
        }
    }

    @Override
    public void connected(NetworkParticipant networkParticipant) {
        if (networkParticipant instanceof NodeParticipant) {
            PeepoCloudNode.getInstance().getServerNodes().put(networkParticipant.getName(), (NodeParticipant) networkParticipant);
        }
    }
}
