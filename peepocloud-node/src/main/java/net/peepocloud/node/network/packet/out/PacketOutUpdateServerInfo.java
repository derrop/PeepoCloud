package net.peepocloud.node.network.packet.out;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketOutUpdateServerInfo extends JsonPacket {

    public PacketOutUpdateServerInfo(MinecraftServerInfo oldInfo, MinecraftServerInfo newInfo) {
        super(8);
        super.setSimpleJsonObject(new SimpleJsonObject().append("oldInfo", oldInfo).append("newInfo", newInfo));
    }
}
