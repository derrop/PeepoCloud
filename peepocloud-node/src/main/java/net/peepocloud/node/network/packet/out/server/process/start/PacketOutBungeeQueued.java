package net.peepocloud.node.network.packet.out.server.process.start;
/*
 * Created by Mc_Ruben on 28.12.2018
 */

import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketOutBungeeQueued extends SerializationPacket {
    public PacketOutBungeeQueued(BungeeCordProxyInfo serverInfo) {
        super(36, serverInfo);
    }
}
