package net.peepocloud.node.network.packet.in.server.process.start;
/*
 * Created by Mc_Ruben on 06.12.2018
 */

import net.peepocloud.node.api.event.network.bungeecord.BungeeStartEvent;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.network.packet.out.api.server.PacketOutAPIProxyStarted;
import net.peepocloud.node.network.participant.NodeParticipantImpl;
import java.util.function.Consumer;

public class PacketInBungeeProcessStarted extends JsonPacketHandler {

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        if (!(networkParticipant instanceof NodeParticipantImpl))
            return;
        BungeeCordProxyInfo proxyInfo = packet.getSimpleJsonObject().getObject("proxyInfo", BungeeCordProxyInfo.class);
        if (proxyInfo.getParentComponentName().equals(networkParticipant.getName())) {
            ((NodeParticipantImpl) networkParticipant).getStartingProxies().put(proxyInfo.getComponentName(), proxyInfo);
            ((NodeParticipantImpl) networkParticipant).getWaitingProxies().remove(proxyInfo.getComponentName());
        }
        PeepoCloudNode.getInstance().getEventManager().callEvent(new BungeeStartEvent(proxyInfo));
        PeepoCloudNode.getInstance().sendPacketToServersAndProxies(new PacketOutAPIProxyStarted(proxyInfo));
    }

    @Override
    public int getId() {
        return 17;
    }
}