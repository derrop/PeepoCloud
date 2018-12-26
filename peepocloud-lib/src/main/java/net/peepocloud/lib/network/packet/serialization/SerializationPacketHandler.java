package net.peepocloud.lib.network.packet.serialization;
/*
 * Created by Mc_Ruben on 26.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;

import java.util.function.Consumer;

public abstract class SerializationPacketHandler<O extends PacketSerializable> implements PacketHandler<SerializationPacket> {
    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, SerializationPacket packet, Consumer<Packet> queryResponse) {
        if (packet.getSerializable() != null) {
            O o = (O) packet.getSerializable();
            this.handle(networkParticipant, o, packet, queryResponse);
        }
    }

    public abstract void handle(NetworkPacketSender packetSender, O o, SerializationPacket packet, Consumer<Packet> queryResponse);
}
