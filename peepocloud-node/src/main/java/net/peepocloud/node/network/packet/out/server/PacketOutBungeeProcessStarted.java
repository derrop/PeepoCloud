package net.peepocloud.node.network.packet.out.server;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;

public class PacketOutBungeeProcessStarted extends JsonPacket {
    public PacketOutBungeeProcessStarted(BungeeCordProxyInfo proxyInfo) {
        super(17);
        this.setSimpleJsonObject(new SimpleJsonObject().append("proxyInfo", proxyInfo));
    }
}
