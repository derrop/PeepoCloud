package net.peepocloud.plugin.network.packet.in;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

import java.util.function.Consumer;

public class PacketInProxyInfo implements PacketHandler<SerializationPacket> {
    @Override
    public int getId() {
        return 8;
    }

    @Override
    public Class<SerializationPacket> getPacketClass() {
        return SerializationPacket.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, SerializationPacket packet, Consumer<Packet> queryResponse) {
        if (!(packet.getSerializable() instanceof BungeeCordProxyInfo))
            return;

        BungeeCordProxyInfo proxyInfo = (BungeeCordProxyInfo) packet.getSerializable();

        //TODO
    }
}
