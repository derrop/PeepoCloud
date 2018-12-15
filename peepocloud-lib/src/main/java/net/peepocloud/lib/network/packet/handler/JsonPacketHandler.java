package net.peepocloud.lib.network.packet.handler;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;

import java.util.function.Consumer;

public abstract class JsonPacketHandler implements PacketHandler {
    @Override
    public final void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        if (!(packet instanceof JsonPacket))
            return;
        handlePacket(networkParticipant, (JsonPacket) packet, queryResponse);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return JsonPacket.class;
    }

    public abstract void handlePacket(NetworkParticipant networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse);
}
