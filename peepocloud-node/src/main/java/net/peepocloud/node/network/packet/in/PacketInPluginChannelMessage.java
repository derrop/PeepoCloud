package net.peepocloud.node.network.packet.in;

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.pluginchannelmessage.ReceivedPluginChannelMessageEvent;
import java.util.function.Consumer;

public class PacketInPluginChannelMessage extends JsonPacketHandler {

    @Override
    public int getId() {
        return 250;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        SimpleJsonObject simpleJsonObject = packet.getSimpleJsonObject();
        if(simpleJsonObject == null || !simpleJsonObject.contains("senderComponent") || !simpleJsonObject.contains("targetType")
                || !simpleJsonObject.contains("identifier") || !simpleJsonObject.contains("message") || !simpleJsonObject.contains("data"))
            return;

        ReceivedPluginChannelMessageEvent event = PeepoCloudNode.getInstance().getEventManager().callEvent(
                new ReceivedPluginChannelMessageEvent(
                        simpleJsonObject.getString("senderComponent"),
                        simpleJsonObject.getObject("targetType", NetworkComponentType.class),
                        simpleJsonObject.getString("identifier"),
                        simpleJsonObject.getString("message"),
                        simpleJsonObject.getJsonObject("data"),
                        simpleJsonObject.getObject("targetComponents", String[].class)
                )
        );

        if(!event.isCancelled()) {
            String senderComponent = event.getSenderComponent();
            String identifier = event.getIdentifier();
            String message = event.getMessage();
            SimpleJsonObject data = event.getData();
            String[] targetComponents = event.getTargetComponents();

            if(event.getTargetType() == NetworkComponentType.MINECRAFT_SERVER)
                PeepoCloudNode.getInstance().getPluginChannelMessageManager().sendBukkitPluginChannelMessage(
                        senderComponent,
                        identifier,
                        message,
                        data,
                        targetComponents
                );
            else
                PeepoCloudNode.getInstance().getPluginChannelMessageManager().sendBungeeCordPluginChannelMessage(
                        senderComponent,
                        identifier,
                        message,
                        data,
                        targetComponents
                );
        }
    }
}
