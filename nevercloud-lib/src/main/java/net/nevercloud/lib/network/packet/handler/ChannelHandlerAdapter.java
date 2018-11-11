package net.nevercloud.lib.network.packet.handler;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.packet.Packet;

public class ChannelHandlerAdapter implements ChannelHandler {
    @Override
    public void connected(NetworkParticipant networkParticipant) {
    }

    @Override
    public void disconnected(NetworkParticipant networkParticipant) {
    }

    @Override
    public void exception(NetworkParticipant networkParticipant, Throwable cause) {
    }

    @Override
    public boolean packet(NetworkParticipant networkParticipant, Packet packet) {
        return false;
    }
}
