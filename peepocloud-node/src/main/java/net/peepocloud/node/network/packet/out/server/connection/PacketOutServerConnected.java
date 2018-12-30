package net.peepocloud.node.network.packet.out.server.connection;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketOutServerConnected extends SerializationPacket {
    public PacketOutServerConnected(MinecraftServerInfo serverInfo) {
        super(33, serverInfo);
    }
}
