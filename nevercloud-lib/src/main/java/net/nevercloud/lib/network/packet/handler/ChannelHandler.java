package net.nevercloud.lib.network.packet.handler;


import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.packet.Packet;

public interface ChannelHandler {

    void connected(NetworkParticipant networkParticipant);
    void disconnected(NetworkParticipant networkParticipant);
    void exception(NetworkParticipant networkParticipant, Throwable cause);
    boolean packet(NetworkParticipant networkParticipant, Packet packet);


}
