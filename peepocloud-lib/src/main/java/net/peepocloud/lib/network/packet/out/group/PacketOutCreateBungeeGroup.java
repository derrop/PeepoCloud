package net.peepocloud.lib.network.packet.out.group;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.bungee.BungeeGroup;

public class PacketOutCreateBungeeGroup extends JsonPacket {
    public PacketOutCreateBungeeGroup(int id) {
        super(id);
    }

    public PacketOutCreateBungeeGroup(BungeeGroup group) {
        super(12);
        this.setSimpleJsonObject(new SimpleJsonObject().append("group", group));
    }
}
