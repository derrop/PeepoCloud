package net.nevercloud.node.network.packet.in;
/*
 * Created by Mc_Ruben on 06.12.2018
 */

import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.handler.PacketHandler;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.event.network.minecraftserver.ServerStartEvent;

import java.util.function.Consumer;

public class PacketInServerProcessStarted implements PacketHandler {
    @Override
    public void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        if (!(packet instanceof JsonPacket))
            return;

        MinecraftServerInfo serverInfo = ((JsonPacket) packet).getSimpleJsonObject().getObject("serverInfo", MinecraftServerInfo.class);
        NeverCloudNode.getInstance().getEventManager().callEvent(new ServerStartEvent(serverInfo));
    }
}
