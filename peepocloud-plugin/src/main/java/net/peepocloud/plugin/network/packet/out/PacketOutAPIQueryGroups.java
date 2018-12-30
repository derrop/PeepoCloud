package net.peepocloud.plugin.network.packet.out;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.JsonPacket;

public class PacketOutAPIQueryGroups extends JsonPacket {


    public PacketOutAPIQueryGroups(NetworkComponentType type) {
        super(202);
        super.setSimpleJsonObject(new SimpleJsonObject().append("type", type));
    }
}
