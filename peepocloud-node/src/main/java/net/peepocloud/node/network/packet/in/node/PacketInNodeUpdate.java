package net.peepocloud.node.network.packet.in.node;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.EmptyPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.node.PeepoCloudNode;

import java.util.function.Consumer;

public class PacketInNodeUpdate implements PacketHandler<EmptyPacket> {
    @Override
    public int getId() {
        return -3;
    }

    @Override
    public Class<EmptyPacket> getPacketClass() {
        return EmptyPacket.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, EmptyPacket packet, Consumer<Packet> queryResponse) {
        PeepoCloudNode.getInstance().installUpdatesSync(PeepoCloudNode.getInstance().getCommandManager().getConsole());
    }
}
