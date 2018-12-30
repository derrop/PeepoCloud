package net.peepocloud.node.network.packet.in.server.connection;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.network.NodeParticipant;

import java.util.function.Consumer;

public class PacketInServerConnected implements PacketHandler<SerializationPacket> {
    @Override
    public int getId() {
        return 33;
    }

    @Override
    public Class<SerializationPacket> getPacketClass() {
        return SerializationPacket.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, SerializationPacket packet, Consumer<Packet> queryResponse) {
        if (!(networkParticipant instanceof NodeParticipant) || !(packet.getSerializable() instanceof MinecraftServerInfo))
            return;

        NodeParticipant participant = (NodeParticipant) networkParticipant;
        MinecraftServerInfo serverInfo = (MinecraftServerInfo) packet.getSerializable();

        participant.getServers().put(serverInfo.getComponentName(), serverInfo);
        participant.getStartingServers().remove(serverInfo.getComponentName());

        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("network.connect.server.other")
                .replace("%name%", serverInfo.getComponentName()).replace("%memory%", Integer.toString(serverInfo.getMemory()))
                .replace("%node%", serverInfo.getParentComponentName())
        );
    }
}
