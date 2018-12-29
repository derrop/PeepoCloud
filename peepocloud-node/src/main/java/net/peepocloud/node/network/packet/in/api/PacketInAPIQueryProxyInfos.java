package net.peepocloud.node.network.packet.in.api;

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.PeepoCloudNode;
import java.util.Collection;
import java.util.function.Consumer;

public class PacketInAPIQueryProxyInfos extends JsonPacketHandler {

    @Override
    public int getId() {
        return 201;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        SimpleJsonObject simpleJsonObject = packet.getSimpleJsonObject();
        Collection<BungeeCordProxyInfo> proxyInfos;
        if(simpleJsonObject.contains("group")) {
            String group = simpleJsonObject.getString("group");
            proxyInfos = simpleJsonObject.getBoolean("onlyStarted")
                    ? PeepoCloudNode.getInstance().getStartedBungeeProxies(group) : PeepoCloudNode.getInstance().getStartedBungeeProxies(group);
        } else {
            proxyInfos = simpleJsonObject.getBoolean("onlyStarted")
                    ? PeepoCloudNode.getInstance().getStartedBungeeProxies() : PeepoCloudNode.getInstance().getStartedBungeeProxies();
        }
        JsonPacket response = new JsonPacket(-2);
        response.setSimpleJsonObject(new SimpleJsonObject().append("proxyInfos", proxyInfos));
        queryResponse.accept(response);
    }


}
