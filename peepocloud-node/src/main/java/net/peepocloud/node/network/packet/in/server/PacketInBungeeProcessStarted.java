package net.peepocloud.node.network.packet.in.server;
/*
 * Created by Mc_Ruben on 06.12.2018
 */

import net.peepocloud.api.event.network.bungeecord.BungeeStartEvent;
import net.peepocloud.api.network.NetworkPacketSender;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.api.network.packet.JsonPacket;
import net.peepocloud.api.network.packet.Packet;
import net.peepocloud.api.network.packet.handler.JsonPacketHandler;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.network.participant.NodeParticipant;

import java.util.function.Consumer;

public class PacketInBungeeProcessStarted extends JsonPacketHandler {
    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        if (!(networkParticipant instanceof NodeParticipant))
            return;
        BungeeCordProxyInfo proxyInfo = packet.getSimpleJsonObject().getObject("proxyInfo", BungeeCordProxyInfo.class);
        if (proxyInfo.getParentComponentName().equals(networkParticipant.getName())) {
            ((NodeParticipant) networkParticipant).getStartingProxies().put(proxyInfo.getComponentName(), proxyInfo);
            ((NodeParticipant) networkParticipant).getWaitingProxies().remove(proxyInfo.getComponentName());
        }
        PeepoCloudNode.getInstance().getEventManager().callEvent(new BungeeStartEvent(proxyInfo));
    }

    @Override
    public int getId() {
        return 17;
    }
}