package net.peepocloud.node.network.packet.out.screen;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;

public class PacketOutScreenLine extends JsonPacket {
    public PacketOutScreenLine(int id) {
        super(id);
    }

    public PacketOutScreenLine(String componentName, String line) {
        super(32);
        this.setSimpleJsonObject(new SimpleJsonObject().append("componentName", componentName).append("line", line));
    }
}
