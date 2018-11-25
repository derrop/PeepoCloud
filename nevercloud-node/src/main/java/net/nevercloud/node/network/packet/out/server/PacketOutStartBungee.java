package net.nevercloud.node.network.packet.out.server;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;

public class PacketOutStartBungee extends JsonPacket {
    public PacketOutStartBungee(BungeeCordProxyInfo proxyInfo) {
        super(11);
        this.setSimpleJsonObject(new SimpleJsonObject().append("proxyInfo", proxyInfo));
    }
}
