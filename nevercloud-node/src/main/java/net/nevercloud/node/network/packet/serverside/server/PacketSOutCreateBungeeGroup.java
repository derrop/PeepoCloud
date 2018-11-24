package net.nevercloud.node.network.packet.serverside.server;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.server.BungeeGroup;
import net.nevercloud.lib.server.MinecraftGroup;

public class PacketSOutCreateBungeeGroup extends JsonPacket {
    public PacketSOutCreateBungeeGroup(int id) {
        super(id);
    }

    public PacketSOutCreateBungeeGroup(BungeeGroup group) {
        super(12);
        this.setSimpleJsonObject(new SimpleJsonObject().append("group", group));
    }
}
