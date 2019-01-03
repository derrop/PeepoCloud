package net.peepocloud.plugin.network.packet.auth;
/*
 * Created by Mc_Ruben on 03.01.2019
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;

import java.util.function.Consumer;

public class PacketInAuthSuccessful implements PacketHandler<Packet> {
    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Class<Packet> getPacketClass() {
        return Packet.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        //TODO
    }
}
