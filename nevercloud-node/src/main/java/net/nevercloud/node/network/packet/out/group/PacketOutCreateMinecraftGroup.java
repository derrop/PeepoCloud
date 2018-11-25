package net.nevercloud.node.network.packet.out.group;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.server.minecraft.MinecraftGroup;

public class PacketOutCreateMinecraftGroup extends JsonPacket {
    public PacketOutCreateMinecraftGroup(MinecraftGroup group) {
        super(12);
        this.setSimpleJsonObject(new SimpleJsonObject().append("group", group));
    }
}
