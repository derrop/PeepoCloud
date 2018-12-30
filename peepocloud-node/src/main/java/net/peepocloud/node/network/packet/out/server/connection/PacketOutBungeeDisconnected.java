package net.peepocloud.node.network.packet.out.server.connection;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

public class PacketOutBungeeDisconnected extends SerializationPacket {
    public PacketOutBungeeDisconnected(BungeeCordProxyInfo proxyInfo) {
        super(29, proxyInfo);
    }
}
