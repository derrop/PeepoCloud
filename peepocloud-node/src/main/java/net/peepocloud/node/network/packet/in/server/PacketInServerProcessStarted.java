package net.peepocloud.node.network.packet.in.server;
/*
 * Created by Mc_Ruben on 06.12.2018
 */

import net.peepocloud.node.api.event.network.minecraftserver.ServerStartEvent;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.network.participant.NodeParticipant;

import java.util.function.Consumer;

public class PacketInServerProcessStarted extends JsonPacketHandler {
    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        if (!(networkParticipant instanceof NodeParticipant))
            return;
        MinecraftServerInfo serverInfo = packet.getSimpleJsonObject().getObject("serverInfo", MinecraftServerInfo.class);
        if (serverInfo.getParentComponentName().equals(networkParticipant.getName())) {
            ((NodeParticipant) networkParticipant).getStartingServers().put(serverInfo.getComponentName(), serverInfo);
            ((NodeParticipant) networkParticipant).getWaitingServers().remove(serverInfo.getComponentName());
        }
        PeepoCloudNode.getInstance().getEventManager().callEvent(new ServerStartEvent(serverInfo));
    }

    @Override
    public int getId() {
        return 18;
    }
}
