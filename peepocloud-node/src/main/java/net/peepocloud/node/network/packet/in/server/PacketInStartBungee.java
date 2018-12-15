package net.peepocloud.node.network.packet.in.server;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.PeepoCloudNode;

import java.util.function.Consumer;

public class PacketInStartBungee extends JsonPacketHandler {
    @Override
    public void handlePacket(NetworkParticipant networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        PeepoCloudNode.getInstance().startBungeeProxy(packet.getSimpleJsonObject().getObject("proxyInfo", BungeeCordProxyInfo.class));
    }

    @Override
    public int getId() {
        return 11;
    }
}
