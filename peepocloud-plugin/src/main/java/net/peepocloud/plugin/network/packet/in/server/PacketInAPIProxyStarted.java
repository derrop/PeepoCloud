package net.peepocloud.plugin.network.packet.in.server;

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.plugin.PeepoCloudPlugin;
import java.util.function.Consumer;

public class PacketInAPIProxyStarted extends JsonPacketHandler {

    @Override
    public int getId() {
        return 102;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        if(packet.getSimpleJsonObject() == null || !packet.getSimpleJsonObject().contains("proxyInfo"))
            return;
        BungeeCordProxyInfo proxyInfo = packet.getSimpleJsonObject().getObject("proxyInfo", BungeeCordProxyInfo.class);
        PeepoCloudPlugin.getInstance().getNetworkHandlers().forEach(handler -> handler.handleProxyAdd(proxyInfo));
    }


}
