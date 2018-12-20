package net.peepocloud.node.network.packet.in.screen;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.node.PeepoCloudNode;

import java.util.function.Consumer;

public class PacketInScreenLine extends JsonPacketHandler {
    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        PeepoCloudNode.getInstance().getScreenManager().getNetworkScreenManager().dispatchScreenInput(
                networkParticipant,
                packet.getSimpleJsonObject().getString("componentName"),
                packet.getSimpleJsonObject().getString("line")
        );
    }

    @Override
    public int getId() {
        return 32;
    }
}
