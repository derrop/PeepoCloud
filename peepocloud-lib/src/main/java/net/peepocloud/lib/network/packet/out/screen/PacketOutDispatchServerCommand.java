package net.peepocloud.lib.network.packet.out.screen;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;

public class PacketOutDispatchServerCommand extends JsonPacket {
    public PacketOutDispatchServerCommand(String componentName, String command) {
        super(31);
        this.setSimpleJsonObject(new SimpleJsonObject().append("componentName", componentName).append("command", command));
    }
}
