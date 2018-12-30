package net.peepocloud.node.network.packet.out.server.process.start;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

public class PacketOutStartBungee extends JsonPacket {
    public PacketOutStartBungee(BungeeCordProxyInfo proxyInfo) {
        super(11);
        this.setSimpleJsonObject(new SimpleJsonObject().append("proxyInfo", proxyInfo));
    }
}
