package net.peepocloud.node.network.packet.out.api.server;

import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;

public class PacketOutAPIServerStarted extends JsonPacket {


    public PacketOutAPIServerStarted(MinecraftServerInfo serverInfo) {
        super(100);
        super.setSimpleJsonObject(new SimpleJsonObject().append("serverInfo", serverInfo));
    }
}
