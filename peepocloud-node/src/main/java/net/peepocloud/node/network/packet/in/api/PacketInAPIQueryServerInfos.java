package net.peepocloud.node.network.packet.in.api;

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.PeepoCloudNode;
import java.util.Collection;
import java.util.function.Consumer;

public class PacketInAPIQueryServerInfos extends JsonPacketHandler {


    @Override
    public int getId() {
        return 200;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        SimpleJsonObject simpleJsonObject = packet.getSimpleJsonObject();
        Collection<MinecraftServerInfo> serverInfos;
        if(simpleJsonObject.contains("group")) {
            String group = simpleJsonObject.getString("group");
            serverInfos = simpleJsonObject.getBoolean("onlyStarted")
                    ? PeepoCloudNode.getInstance().getStartedMinecraftServers(group) : PeepoCloudNode.getInstance().getMinecraftServers(group);
        } else {
            serverInfos = simpleJsonObject.getBoolean("onlyStarted")
                    ? PeepoCloudNode.getInstance().getStartedMinecraftServers() : PeepoCloudNode.getInstance().getMinecraftServers();
        }
        JsonPacket response = new JsonPacket(-2);
        response.setSimpleJsonObject(new SimpleJsonObject().append("serverInfos", serverInfos));
        queryResponse.accept(response);
    }
}
