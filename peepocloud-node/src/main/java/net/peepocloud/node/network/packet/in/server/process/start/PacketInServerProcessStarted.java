package net.peepocloud.node.network.packet.in.server.process.start;
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
import net.peepocloud.node.network.packet.out.api.server.PacketOutAPIServerStarted;
import net.peepocloud.node.network.participant.NodeParticipantImpl;

import java.util.function.Consumer;

public class PacketInServerProcessStarted extends JsonPacketHandler {

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        if (!(networkParticipant instanceof NodeParticipantImpl))
            return;
        MinecraftServerInfo serverInfo = packet.getSimpleJsonObject().getObject("serverInfo", MinecraftServerInfo.class);
        if (serverInfo.getParentComponentName().equals(networkParticipant.getName())) {
            ((NodeParticipantImpl) networkParticipant).getStartingServers().put(serverInfo.getComponentName(), serverInfo);
            ((NodeParticipantImpl) networkParticipant).getWaitingServers().remove(serverInfo.getComponentName());
        }
        PeepoCloudNode.getInstance().getEventManager().callEvent(new ServerStartEvent(serverInfo));
        PeepoCloudNode.getInstance().sendPacketToServersAndProxies(new PacketOutAPIServerStarted(serverInfo));
    }

    @Override
    public int getId() {
        return 18;
    }
}
