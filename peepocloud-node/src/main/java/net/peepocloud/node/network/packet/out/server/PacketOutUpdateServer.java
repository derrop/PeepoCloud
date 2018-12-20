package net.peepocloud.node.network.packet.out.server;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import net.peepocloud.commons.config.json.SimpleJsonObject;
import net.peepocloud.api.network.packet.JsonPacket;
import net.peepocloud.api.server.minecraft.MinecraftServerInfo;

public class PacketOutUpdateServer extends JsonPacket {
    public PacketOutUpdateServer(MinecraftServerInfo serverInfo) {
        super(15);
        setSimpleJsonObject(new SimpleJsonObject().append("serverInfo", serverInfo));
    }
}
