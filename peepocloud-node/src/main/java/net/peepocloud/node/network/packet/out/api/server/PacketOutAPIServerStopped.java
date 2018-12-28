package net.peepocloud.node.network.packet.out.api.server;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketOutAPIServerStopped extends JsonPacket {

    public PacketOutAPIServerStopped(MinecraftServerInfo serverInfo) {
        super(101);
        super.setSimpleJsonObject(new SimpleJsonObject().append("serverInfo", serverInfo));
    }
}
