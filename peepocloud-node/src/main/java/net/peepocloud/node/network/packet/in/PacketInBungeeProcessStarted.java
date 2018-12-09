package net.peepocloud.node.network.packet.in;
/*
 * Created by Mc_Ruben on 06.12.2018
 */

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.network.bungeecord.BungeeStartEvent;

import java.util.function.Consumer;

public class PacketInBungeeProcessStarted implements PacketHandler {
    @Override
    public void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        if (!(packet instanceof JsonPacket))
            return;

        BungeeCordProxyInfo proxyInfo = ((JsonPacket) packet).getSimpleJsonObject().getObject("proxyInfo", BungeeCordProxyInfo.class);
        PeepoCloudNode.getInstance().getEventManager().callEvent(new BungeeStartEvent(proxyInfo));
    }
}
