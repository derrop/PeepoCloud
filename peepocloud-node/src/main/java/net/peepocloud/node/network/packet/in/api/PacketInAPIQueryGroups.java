package net.peepocloud.node.network.packet.in.api;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.node.PeepoCloudNode;

import java.util.function.Consumer;

public class PacketInAPIQueryGroups extends JsonPacketHandler {

    @Override
    public int getId() {
        return 202;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        SimpleJsonObject simpleJsonObject = packet.getSimpleJsonObject();
        JsonPacket response = new JsonPacket(-2);

        if(simpleJsonObject != null && simpleJsonObject.contains("type")) {
            NetworkComponentType type = simpleJsonObject.getObject("type", NetworkComponentType.class);

            if (type == NetworkComponentType.MINECRAFT_SERVER)
                response.setSimpleJsonObject(new SimpleJsonObject().append("groups", PeepoCloudNode.getInstance().getMinecraftGroups()));
            else if (type == NetworkComponentType.BUNGEECORD)
                response.setSimpleJsonObject(new SimpleJsonObject().append("groups", PeepoCloudNode.getInstance().getBungeeGroups()));
        }

        queryResponse.accept(response);
    }
}
