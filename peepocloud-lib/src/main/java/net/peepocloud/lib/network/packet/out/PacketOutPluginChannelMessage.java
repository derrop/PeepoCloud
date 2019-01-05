package net.peepocloud.lib.network.packet.out;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.JsonPacket;

public class PacketOutPluginChannelMessage extends JsonPacket {


    public PacketOutPluginChannelMessage(String senderComponent, NetworkComponentType targetType, String identifier, String message, SimpleJsonObject data, String[] targetComponents) {
        super(250);
        SimpleJsonObject simpleJsonObject = new SimpleJsonObject()
                .append("senderComponent", senderComponent)
                .append("targetType", targetType)
                .append("identifier", identifier)
                .append("message", message)
                .append("data", data.asJsonObject())
                .append("targetComponents", targetComponents);
        super.setSimpleJsonObject(simpleJsonObject);
    }
}
