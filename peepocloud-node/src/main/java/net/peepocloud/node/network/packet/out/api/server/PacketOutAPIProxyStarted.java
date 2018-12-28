package net.peepocloud.node.network.packet.out.api.server;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

public class PacketOutAPIProxyStarted extends JsonPacket {


    public PacketOutAPIProxyStarted(BungeeCordProxyInfo proxyInfo) {
        super(102);
        super.setSimpleJsonObject(new SimpleJsonObject().append("proxyInfo", proxyInfo));
    }
}
