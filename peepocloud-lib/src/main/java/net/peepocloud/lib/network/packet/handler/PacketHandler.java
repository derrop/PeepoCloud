package net.peepocloud.lib.network.packet.handler;

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.Packet;

import java.util.function.Consumer;

public interface PacketHandler {

    void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse);

}
