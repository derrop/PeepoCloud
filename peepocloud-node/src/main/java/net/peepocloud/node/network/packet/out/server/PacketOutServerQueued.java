package net.peepocloud.node.network.packet.out.server;
/*
 * Created by Mc_Ruben on 28.12.2018
 */

import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketOutServerQueued extends SerializationPacket {
    public PacketOutServerQueued(MinecraftServerInfo serverInfo) {
        super(35, serverInfo);
    }
}
