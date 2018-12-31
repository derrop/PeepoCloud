package net.peepocloud.lib.network.packet.out;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;

public class PacketOutToggleDebug extends JsonPacket {
    public PacketOutToggleDebug(boolean enable) {
        super(-10);
        setSimpleJsonObject(new SimpleJsonObject().append("debug", enable));
    }
}
