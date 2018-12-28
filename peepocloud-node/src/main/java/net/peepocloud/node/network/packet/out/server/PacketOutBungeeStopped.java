package net.peepocloud.node.network.packet.out.server;
/*
 * Created by Mc_Ruben on 28.12.2018
 */

import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

public class PacketOutBungeeStopped extends SerializationPacket {
    public PacketOutBungeeStopped(BungeeCordProxyInfo proxyInfo) {
        super(37, proxyInfo);
    }
}
