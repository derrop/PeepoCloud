package net.peepocloud.node.network.packet.out.server;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketOutServerProcessStarted extends JsonPacket {
    public PacketOutServerProcessStarted(MinecraftServerInfo serverInfo) {
        super(18);
        this.setSimpleJsonObject(new SimpleJsonObject().append("serverInfo", serverInfo));
    }
}
