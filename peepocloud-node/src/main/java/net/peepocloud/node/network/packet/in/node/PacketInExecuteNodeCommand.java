package net.peepocloud.node.network.packet.in.node;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.network.packet.out.node.PacketOutExecuteNodeCommand;

import java.util.function.Consumer;

public class PacketInExecuteNodeCommand implements PacketHandler<PacketOutExecuteNodeCommand> {
    @Override
    public int getId() {
        return 5;
    }

    @Override
    public Class<PacketOutExecuteNodeCommand> getPacketClass() {
        return PacketOutExecuteNodeCommand.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, PacketOutExecuteNodeCommand packet, Consumer<Packet> queryResponse) {
        PeepoCloudNode.getInstance().getCommandManager().dispatchCommand(PeepoCloudNode.getInstance().getCommandManager().getConsole(), packet.getCommand());
    }
}
