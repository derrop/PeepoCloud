package net.peepocloud.lib.network.packet.out.server;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketOutUpdateServer extends JsonPacket {
    public PacketOutUpdateServer(MinecraftServerInfo serverInfo) {
        super(15);
        setSimpleJsonObject(new SimpleJsonObject().append("serverInfo", serverInfo));
    }
}
