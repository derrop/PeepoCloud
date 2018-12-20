package net.peepocloud.lib.network.packet.handler;


import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.api.network.packet.Packet;

public interface ChannelHandler {

    void connected(NetworkParticipant networkParticipant);
    void disconnected(NetworkParticipant networkParticipant);
    void exception(NetworkParticipant networkParticipant, Throwable cause);
    boolean packet(NetworkParticipant networkParticipant, Packet packet);


}
