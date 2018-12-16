package net.peepocloud.node.network.packet.in.group;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.api.server.bungee.BungeeGroup;
import net.peepocloud.node.PeepoCloudNode;

import java.util.function.Consumer;

public class PacketInCreateBungeeGroup extends JsonPacketHandler {
    @Override
    public void handlePacket(NetworkParticipant networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        BungeeGroup group = packet.getSimpleJsonObject().getObject("group", BungeeGroup.class);
        PeepoCloudNode.getInstance().getBungeeGroups().put(group.getName(), group);
    }

    @Override
    public int getId() {
        return 12;
    }
}
