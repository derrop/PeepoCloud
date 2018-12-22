package net.peepocloud.lib.network.packet.out.server;
/*
 * Created by Mc_Ruben on 25.11.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketOutStopServer extends JsonPacket {

    public PacketOutStopServer(MinecraftServerInfo serverInfo) {
        super(17);
        super.setSimpleJsonObject(new SimpleJsonObject().append("serverInfo", serverInfo));
    }
}
