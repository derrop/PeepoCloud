package net.peepocloud.node.network.packet.in.server;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.api.network.NetworkPacketSender;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.api.network.packet.JsonPacket;
import net.peepocloud.api.network.packet.Packet;
import net.peepocloud.api.network.packet.handler.JsonPacketHandler;
import net.peepocloud.api.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.PeepoCloudNode;

import java.util.function.Consumer;

public class PacketInStartServer extends JsonPacketHandler {
    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        PeepoCloudNode.getInstance().startMinecraftServer(packet.getSimpleJsonObject().getObject("serverInfo", MinecraftServerInfo.class));
    }

    @Override
    public int getId() {
        return 10;
    }
}
