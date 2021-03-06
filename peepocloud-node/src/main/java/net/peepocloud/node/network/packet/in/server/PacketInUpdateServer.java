package net.peepocloud.node.network.packet.in.server;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.server.CloudProcess;
import net.peepocloud.node.network.participant.MinecraftServerParticipantImpl;
import net.peepocloud.node.server.process.ServerProcess;

import java.util.function.Consumer;

public class PacketInUpdateServer extends JsonPacketHandler {
    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        boolean a = false;
        MinecraftServerInfo serverInfo = packet.getSimpleJsonObject().getObject("serverInfo", MinecraftServerInfo.class);
        if (PeepoCloudNode.getInstance().getServersOnThisNode().containsKey(serverInfo.getComponentName())) {
            ((MinecraftServerParticipantImpl) PeepoCloudNode.getInstance().getServersOnThisNode().get(serverInfo.getComponentName())).setServerInfo(serverInfo);
            a = true;
        }
        if (PeepoCloudNode.getInstance().getProcessManager().getProcesses().containsKey(serverInfo.getComponentName())) {
            CloudProcess process = PeepoCloudNode.getInstance().getProcessManager().getProcesses().get(serverInfo.getComponentName());
            if (process.isServer()) {
                ((ServerProcess) process).setServerInfo(serverInfo);
                a = true;
            }
        }
        if (a) {
            //TODO send api update packet to all components
        }
    }

    @Override
    public int getId() {
        return 15;
    }
}
