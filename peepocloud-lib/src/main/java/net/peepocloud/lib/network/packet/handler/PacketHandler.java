package net.peepocloud.lib.network.packet.handler;

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.Packet;

import java.util.function.Consumer;

public interface PacketHandler<P extends Packet> {

    int getId();

    Class<P> getPacketClass();

    default void handleInternal(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        if(packet.getClass().equals(this.getPacketClass()))
            this.handlePacket(networkParticipant, (P) packet, queryResponse);
    }

    void handlePacket(NetworkParticipant networkParticipant, P packet, Consumer<Packet> queryResponse);


}
