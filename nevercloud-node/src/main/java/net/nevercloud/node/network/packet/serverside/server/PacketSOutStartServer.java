package net.nevercloud.node.network.packet.serverside.server;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketSOutStartServer extends JsonPacket {
    public PacketSOutStartServer(MinecraftServerInfo serverInfo) {
        super(10);
        this.setSimpleJsonObject(new SimpleJsonObject().append("serverInfo", serverInfo));
    }
}
