package net.peepocloud.node.network.packet.out.screen;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;

public class PacketOutDispatchProxyCommand extends JsonPacket {
    public PacketOutDispatchProxyCommand(String componentName, String command) {
        super(30);
        this.setSimpleJsonObject(new SimpleJsonObject().append("componentName", componentName).append("command", command));
    }
}
