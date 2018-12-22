package net.peepocloud.node.network;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.network.bungeecord.BungeeDisconnectEvent;
import net.peepocloud.node.api.event.network.minecraftserver.ServerDisconnectEvent;
import net.peepocloud.node.api.event.network.node.NodeDisconnectEvent;
import net.peepocloud.node.network.participant.BungeeCordParticipantImpl;
import net.peepocloud.node.network.participant.MinecraftServerParticipantImpl;
import net.peepocloud.node.network.participant.NodeParticipantImpl;
import net.peepocloud.node.utility.NodeUtils;

public class ServerDefaultHandler extends ChannelHandlerAdapter {
    @Override
    public void disconnected(NetworkParticipant networkParticipant) {
        if (networkParticipant instanceof NodeParticipantImpl) {
            PeepoCloudNode.getInstance().getEventManager().callEvent(new NodeDisconnectEvent((NodeParticipantImpl) networkParticipant));
            PeepoCloudNode.getInstance().getScreenManager().getNetworkScreenManager().handleNodeDisconnect((NodeParticipantImpl) networkParticipant);
            PeepoCloudNode.getInstance().getNetworkServer().handleNodeDisconnect((NodeParticipantImpl) networkParticipant);
            NodeUtils.updateNodeInfoForSupport(null);
        } else if (networkParticipant instanceof BungeeCordParticipantImpl) {
            BungeeCordParticipantImpl participant = (BungeeCordParticipantImpl) networkParticipant;
            PeepoCloudNode.getInstance().getProxiesOnThisNode().remove(participant.getName());
            PeepoCloudNode.getInstance().getEventManager().callEvent(new BungeeDisconnectEvent(participant));
        } else if (networkParticipant instanceof MinecraftServerParticipantImpl) {
            MinecraftServerParticipantImpl participant = (MinecraftServerParticipantImpl) networkParticipant;
            PeepoCloudNode.getInstance().getServersOnThisNode().remove(participant.getName());
            PeepoCloudNode.getInstance().getEventManager().callEvent(new ServerDisconnectEvent(participant));
        }
        PeepoCloudNode.getInstance().getLogger().debug("Participant [" + networkParticipant.getName() + "/" + networkParticipant.getAddress() + "] disconnected (" + networkParticipant.getClass().getSimpleName() + ")");
    }

    @Override
    public boolean packet(NetworkParticipant networkParticipant, Packet packet) {
        if (PeepoCloudNode.getInstance().getLogger().isDebugging()) {
            PeepoCloudNode.getInstance().getLogger().debug("Receiving packet [id=" + packet.getId() + "/queryUniqueId=" + packet.getQueryUUID() + "] from " + networkParticipant.getName());
        }
        return false;
    }
}
