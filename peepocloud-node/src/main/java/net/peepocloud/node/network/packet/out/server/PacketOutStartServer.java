package net.peepocloud.node.network.packet.out.server;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.peepocloud.commons.config.json.SimpleJsonObject;
import net.peepocloud.api.network.packet.JsonPacket;
import net.peepocloud.api.server.minecraft.MinecraftServerInfo;

public class PacketOutStartServer extends JsonPacket {
    public PacketOutStartServer(MinecraftServerInfo serverInfo) {
        super(10);
        this.setSimpleJsonObject(new SimpleJsonObject().append("serverInfo", serverInfo));
    }
}
