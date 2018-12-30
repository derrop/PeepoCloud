package net.peepocloud.node.network.packet.out;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

public class PacketOutProxyInfo extends SerializationPacket {
    public PacketOutProxyInfo(BungeeCordProxyInfo proxyInfo) {
        super(8, proxyInfo);
    }
}
