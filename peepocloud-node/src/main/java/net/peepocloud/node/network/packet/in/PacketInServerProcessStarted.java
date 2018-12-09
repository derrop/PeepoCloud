package net.peepocloud.node.network.packet.in;
/*
 * Created by Mc_Ruben on 06.12.2018
 */

import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.network.minecraftserver.ServerStartEvent;

import java.util.function.Consumer;

public class PacketInServerProcessStarted implements PacketHandler {
    @Override
    public void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        if (!(packet instanceof JsonPacket))
            return;

        MinecraftServerInfo serverInfo = ((JsonPacket) packet).getSimpleJsonObject().getObject("serverInfo", MinecraftServerInfo.class);
        PeepoCloudNode.getInstance().getEventManager().callEvent(new ServerStartEvent(serverInfo));
    }
}
