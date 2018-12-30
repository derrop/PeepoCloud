package net.peepocloud.node.network.packet.out;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketOutServerInfo extends SerializationPacket {
    public PacketOutServerInfo(MinecraftServerInfo serverInfo) {
        super(9, serverInfo);
    }
}
