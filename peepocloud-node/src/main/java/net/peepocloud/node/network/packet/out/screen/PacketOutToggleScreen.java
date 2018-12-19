package net.peepocloud.node.network.packet.out.screen;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import net.peepocloud.commons.config.json.SimpleJsonObject;
import net.peepocloud.api.network.packet.JsonPacket;

public class PacketOutToggleScreen extends JsonPacket {
    public PacketOutToggleScreen(int id) {
        super(id);
    }

    public PacketOutToggleScreen(String componentName, boolean enable) {
        super(20);
        setSimpleJsonObject(new SimpleJsonObject().append("name", componentName).append("enable", enable));
    }
}
