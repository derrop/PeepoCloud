package net.nevercloud.node.network.packet.serverside.server;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.server.MinecraftGroup;

public class PacketSOutCreateMinecraftGroup extends JsonPacket {
    public PacketSOutCreateMinecraftGroup(MinecraftGroup group) {
        super(12);
        this.setSimpleJsonObject(new SimpleJsonObject().append("group", group));
    }
}
