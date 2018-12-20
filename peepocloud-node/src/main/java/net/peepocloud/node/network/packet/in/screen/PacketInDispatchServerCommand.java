package net.peepocloud.node.network.packet.in.screen;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.api.network.NetworkPacketSender;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.api.network.packet.JsonPacket;
import net.peepocloud.api.network.packet.Packet;
import net.peepocloud.api.network.packet.handler.JsonPacketHandler;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.server.process.CloudProcess;

import java.util.function.Consumer;

public class PacketInDispatchServerCommand extends JsonPacketHandler {
    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        CloudProcess process = PeepoCloudNode.getInstance().getProcessManager().getProcesses().get(packet.getSimpleJsonObject().getString("componentName"));
        if (process != null)
            process.dispatchCommand(packet.getSimpleJsonObject().getString("command"));
    }

    @Override
    public int getId() {
        return 31;
    }
}
