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
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.network.bungeecord.BungeeStopEvent;
import net.peepocloud.node.api.event.network.minecraftserver.ServerStopEvent;
import net.peepocloud.node.api.network.NodeParticipant;
import net.peepocloud.node.network.participant.NodeParticipantImpl;

import java.util.function.Consumer;

public class PacketInBungeeStopped implements PacketHandler<SerializationPacket> {
    @Override
    public int getId() {
        return 37;
    }

    @Override
    public Class<SerializationPacket> getPacketClass() {
        return SerializationPacket.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, SerializationPacket packet, Consumer<Packet> queryResponse) {
        if (!(networkParticipant instanceof NodeParticipant) || !(packet.getSerializable() instanceof BungeeCordProxyInfo))
            return;
        NodeParticipantImpl participant = (NodeParticipantImpl) networkParticipant;
        BungeeCordProxyInfo serverInfo = (BungeeCordProxyInfo) packet.getSerializable();
        participant.getProxies().remove(serverInfo.getComponentName());
        participant.getStartingProxies().remove(serverInfo.getComponentName());
        participant.getWaitingProxies().remove(serverInfo.getComponentName());

        PeepoCloudNode.getInstance().getEventManager().callEvent(new BungeeStopEvent(serverInfo));
    }
}
