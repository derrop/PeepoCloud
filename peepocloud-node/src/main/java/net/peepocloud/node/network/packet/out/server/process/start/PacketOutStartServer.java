package net.peepocloud.node.network.packet.out.server.process.start;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketOutStartServer extends JsonPacket {
    public PacketOutStartServer(MinecraftServerInfo serverInfo) {
        super(10);
        this.setSimpleJsonObject(new SimpleJsonObject().append("serverInfo", serverInfo));
    }
}
