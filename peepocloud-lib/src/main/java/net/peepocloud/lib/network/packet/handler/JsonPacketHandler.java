package net.peepocloud.api.network.packet.handler;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.api.network.NetworkPacketSender;
import net.peepocloud.api.network.packet.JsonPacket;
import net.peepocloud.api.network.packet.Packet;

import java.util.function.Consumer;

public abstract class JsonPacketHandler implements PacketHandler<JsonPacket> {

    @Override
    public abstract void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse);

    @Override
    public Class<JsonPacket> getPacketClass() {
        return JsonPacket.class;
    }

}
