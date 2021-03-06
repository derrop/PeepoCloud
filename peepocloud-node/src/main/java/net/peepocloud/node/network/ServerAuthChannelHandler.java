package net.peepocloud.node.network;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.peepocloud.node.PeepoCloudNode;

public class ServerAuthChannelHandler extends ChannelHandlerAdapter {

    @Override
    public boolean packet(NetworkParticipant networkParticipant, Packet packet) {
        if(packet.getId() != -1) {
            PeepoCloudNode.getInstance().debug("Received wrong packet during auth-phase ("
                    + packet.getClass().getSimpleName() + "/" + packet.getId() + ")");
            networkParticipant.close();
            return true;
        }
        return false;
    }
}
