package net.peepocloud.node.network.packet.in.group;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.network.packet.out.group.PacketOutMinecraftGroupDeleted;

import java.util.function.Consumer;

public class PacketInMinecraftGroupDeleted implements PacketHandler<PacketOutMinecraftGroupDeleted> {
    @Override
    public int getId() {
        return 2;
    }

    @Override
    public Class<PacketOutMinecraftGroupDeleted> getPacketClass() {
        return PacketOutMinecraftGroupDeleted.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, PacketOutMinecraftGroupDeleted packet, Consumer<Packet> queryResponse) {
        PeepoCloudNode.getInstance().getMinecraftGroups().remove(packet.getName());
    }
}
