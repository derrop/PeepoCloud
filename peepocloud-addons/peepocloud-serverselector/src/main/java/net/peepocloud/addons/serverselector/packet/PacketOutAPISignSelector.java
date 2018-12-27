package net.peepocloud.addons.serverselector.packet;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;

public class PacketOutAPISignSelector extends JsonPacket {


    public PacketOutAPISignSelector(SimpleJsonObject container) {
        super(150);
        super.setSimpleJsonObject(container);
    }
}
