package net.peepocloud.node.network.packet.in;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.network.participant.NodeParticipant;

import java.util.function.Consumer;

public class PacketInScreenLine implements PacketHandler {
    @Override
    public void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        if (!(packet instanceof JsonPacket) || !(networkParticipant instanceof NodeParticipant))
            return;
        PeepoCloudNode.getInstance().getScreenManager()
                .getNetworkScreenManager().dispatchScreenInput(
                (NodeParticipant) networkParticipant,
                ((JsonPacket) packet).getSimpleJsonObject().getString("name"),
                ((JsonPacket) packet).getSimpleJsonObject().getString("line")
        );
    }
}
