package net.peepocloud.api.network.packet.handler;

import net.peepocloud.api.network.NetworkPacketSender;
import net.peepocloud.api.network.packet.Packet;

import java.util.function.Consumer;

public interface PacketHandler<P extends Packet> {

    int getId();

    Class<P> getPacketClass();

    default void handleInternal(NetworkPacketSender networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        if(packet.getClass().equals(this.getPacketClass()))
            this.handlePacket(networkParticipant, (P) packet, queryResponse);
    }

    void handlePacket(NetworkPacketSender networkParticipant, P packet, Consumer<Packet> queryResponse);


}
