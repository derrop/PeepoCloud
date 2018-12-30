package net.peepocloud.node.network.packet.out.server.connection;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketOutServerDisconnected extends SerializationPacket {
    public PacketOutServerDisconnected(MinecraftServerInfo serverInfo) {
        super(28, serverInfo);
    }
}
