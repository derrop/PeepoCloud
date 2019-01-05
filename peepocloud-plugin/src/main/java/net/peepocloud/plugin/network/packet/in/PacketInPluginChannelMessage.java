package net.peepocloud.plugin.network.packet.in;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.plugin.PeepoCloudPlugin;

import java.util.function.Consumer;

public class PacketInPluginChannelMessage extends JsonPacketHandler {


    @Override
    public int getId() {
        return 250;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        SimpleJsonObject simpleJsonObject = packet.getSimpleJsonObject();
        if(simpleJsonObject == null || !simpleJsonObject.contains("senderComponent") || !simpleJsonObject.contains("identifier")
            || !simpleJsonObject.contains("message") || !simpleJsonObject.contains("data"))
            return;

        PeepoCloudPlugin.getInstance().getNetworkHandlers().forEach(networkAPIHandler ->
                networkAPIHandler.handlePluginChannelMessage(
                        simpleJsonObject.getString("senderComponent"),
                        simpleJsonObject.getString("identifier"),
                        simpleJsonObject.getString("message"),
                        simpleJsonObject.getJsonObject("data")
                ));
    }
}
