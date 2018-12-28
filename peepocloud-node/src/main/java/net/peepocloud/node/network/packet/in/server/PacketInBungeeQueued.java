package net.peepocloud.node.network.packet.in.server;
/*
 * Created by Mc_Ruben on 28.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.api.network.NodeParticipant;
import net.peepocloud.node.network.participant.NodeParticipantImpl;

import java.util.function.Consumer;

public class PacketInBungeeQueued implements PacketHandler<SerializationPacket> {
    @Override
    public int getId() {
        return 36;
    }

    @Override
    public Class<SerializationPacket> getPacketClass() {
        return SerializationPacket.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, SerializationPacket packet, Consumer<Packet> queryResponse) {
        if (!(packet.getSerializable() instanceof BungeeCordProxyInfo) || !(networkParticipant instanceof NodeParticipant))
            return;

        BungeeCordProxyInfo serverInfo = (BungeeCordProxyInfo) packet.getSerializable();
        NodeParticipantImpl participant = (NodeParticipantImpl) networkParticipant;
        participant.getWaitingProxies().put(serverInfo.getComponentName(), serverInfo);
    }
}
