package net.peepocloud.node.network.packet.in.server.connection;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.network.NodeParticipant;

import java.util.function.Consumer;

public class PacketInBungeeDisconnected implements PacketHandler<SerializationPacket> {
    @Override
    public int getId() {
        return 29;
    }

    @Override
    public Class<SerializationPacket> getPacketClass() {
        return SerializationPacket.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, SerializationPacket packet, Consumer<Packet> queryResponse) {
        if (!(networkParticipant instanceof NodeParticipant) || !(packet.getSerializable() instanceof BungeeCordProxyInfo))
            return;

        NodeParticipant participant = (NodeParticipant) networkParticipant;
        BungeeCordProxyInfo serverInfo = (BungeeCordProxyInfo) packet.getSerializable();

        participant.getProxies().remove(serverInfo.getComponentName(), serverInfo);
        participant.getStartingProxies().remove(serverInfo.getComponentName());

        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("network.disconnect.bungee.other")
                .replace("%name%", serverInfo.getComponentName()).replace("%memory%", Integer.toString(serverInfo.getMemory()))
                .replace("%node%", serverInfo.getParentComponentName())
        );
    }
}
