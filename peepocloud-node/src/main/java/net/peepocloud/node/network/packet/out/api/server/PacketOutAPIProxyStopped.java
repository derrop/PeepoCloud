package net.peepocloud.node.network.packet.out.api.server;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

public class PacketOutAPIProxyStopped extends JsonPacket {


    public PacketOutAPIProxyStopped(BungeeCordProxyInfo proxyInfo) {
        super(103);
        super.setSimpleJsonObject(new SimpleJsonObject().append("proxyInfo", proxyInfo));
    }
}
