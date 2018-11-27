package net.nevercloud.node.network.packet.in;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.handler.PacketHandler;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.network.participant.NodeParticipant;

import java.util.function.Consumer;

public class PacketInScreenLine implements PacketHandler {
    @Override
    public void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        if (!(packet instanceof JsonPacket) || !(networkParticipant instanceof NodeParticipant))
            return;
        NeverCloudNode.getInstance().getScreenManager()
                .getNetworkScreenManager().dispatchScreenInput(
                (NodeParticipant) networkParticipant,
                ((JsonPacket) packet).getSimpleJsonObject().getString("name"),
                ((JsonPacket) packet).getSimpleJsonObject().getString("line")
        );
    }
}
