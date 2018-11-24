package net.nevercloud.node.network.packet.serverside.server;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.server.BungeeCordProxyInfo;

public class PacketSOutStartBungee extends JsonPacket {
    public PacketSOutStartBungee(BungeeCordProxyInfo proxyInfo) {
        super(11);
        this.setSimpleJsonObject(new SimpleJsonObject().append("proxyInfo", proxyInfo));
    }
}
