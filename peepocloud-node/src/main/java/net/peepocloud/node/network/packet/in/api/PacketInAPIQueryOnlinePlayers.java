package net.peepocloud.node.network.packet.in.api;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.node.PeepoCloudNode;

import java.util.UUID;
import java.util.function.Consumer;

public class PacketInAPIQueryOnlinePlayers extends JsonPacketHandler {


    @Override
    public int getId() {
        return 203;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        SimpleJsonObject simpleJsonObject = packet.getSimpleJsonObject();
        SimpleJsonObject responseObject = new SimpleJsonObject();

        if(simpleJsonObject != null) {
            if(simpleJsonObject.contains("uniqueId")) {
                UUID uniqueId = simpleJsonObject.getObject("uniqueId", UUID.class);
                responseObject.append("player", PeepoCloudNode.getInstance().getPlayer(uniqueId));
            } else if(simpleJsonObject.contains("name")) {
                String name = simpleJsonObject.getString("name");
                responseObject.append("player", PeepoCloudNode.getInstance().getPlayer(name));
            } else if(simpleJsonObject.contains("bungeeGroup")) {
                String group = simpleJsonObject.getString("bungeeGroup");
                responseObject.append("players", PeepoCloudNode.getInstance().getOnlinePlayers(PeepoCloudNode.getInstance().getBungeeGroup(group)));
            } else if(simpleJsonObject.contains("minecraftGroup")) {
                String group = simpleJsonObject.getString("minecraftGroup");
                responseObject.append("players", PeepoCloudNode.getInstance().getOnlinePlayers(PeepoCloudNode.getInstance().getMinecraftGroup(group)));
            } else {
                responseObject.append("players", PeepoCloudNode.getInstance().getOnlinePlayers());
            }
        }
        JsonPacket responsePacket = new JsonPacket(-2);
        responsePacket.setSimpleJsonObject(responseObject);
        queryResponse.accept(responsePacket);
    }
}
