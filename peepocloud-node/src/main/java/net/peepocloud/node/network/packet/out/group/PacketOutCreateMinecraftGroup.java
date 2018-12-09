package net.peepocloud.node.network.packet.out.group;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;

public class PacketOutCreateMinecraftGroup extends JsonPacket {
    public PacketOutCreateMinecraftGroup(MinecraftGroup group) {
        super(12);
        this.setSimpleJsonObject(new SimpleJsonObject().append("group", group));
    }
}
