package net.peepocloud.node.network;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.ChannelHandlerAdapter;

public class ServerAuthChannelHandler extends ChannelHandlerAdapter {

    @Override
    public boolean packet(NetworkParticipant networkParticipant, Packet packet) {
        return packet.getId() != -1;
    }
}
