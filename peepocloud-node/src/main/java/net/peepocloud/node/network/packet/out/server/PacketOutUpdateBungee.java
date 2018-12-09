package net.peepocloud.node.network.packet.out.server;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

public class PacketOutUpdateBungee extends JsonPacket {
    public PacketOutUpdateBungee(BungeeCordProxyInfo proxyInfo) {
        super(16);
        setSimpleJsonObject(new SimpleJsonObject().append("proxyInfo", proxyInfo));
    }
}
