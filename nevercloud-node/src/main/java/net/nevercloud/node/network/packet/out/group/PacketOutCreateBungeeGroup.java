package net.nevercloud.node.network.packet.out.group;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.server.bungee.BungeeGroup;

public class PacketOutCreateBungeeGroup extends JsonPacket {
    public PacketOutCreateBungeeGroup(int id) {
        super(id);
    }

    public PacketOutCreateBungeeGroup(BungeeGroup group) {
        super(12);
        this.setSimpleJsonObject(new SimpleJsonObject().append("group", group));
    }
}
