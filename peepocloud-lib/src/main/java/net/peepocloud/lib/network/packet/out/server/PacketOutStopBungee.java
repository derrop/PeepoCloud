package net.peepocloud.lib.network.packet.out.server;
/*
 * Created by Mc_Ruben on 25.11.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

public class PacketOutStopBungee extends JsonPacket {

    public PacketOutStopBungee(BungeeCordProxyInfo serverInfo) {
        super(18);
        super.setSimpleJsonObject(new SimpleJsonObject().append("serverInfo", serverInfo));
    }
}
