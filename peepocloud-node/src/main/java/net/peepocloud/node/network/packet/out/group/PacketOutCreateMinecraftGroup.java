package net.peepocloud.node.network.packet.out.group;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.peepocloud.commons.config.json.SimpleJsonObject;
import net.peepocloud.api.network.packet.JsonPacket;
import net.peepocloud.api.server.minecraft.MinecraftGroup;

public class PacketOutCreateMinecraftGroup extends JsonPacket {
    public PacketOutCreateMinecraftGroup(MinecraftGroup group) {
        super(13);
        this.setSimpleJsonObject(new SimpleJsonObject().append("group", group));
    }
}
