package net.nevercloud.node.network.packet.in;
/*
 * Created by Mc_Ruben on 06.12.2018
 */

import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.handler.PacketHandler;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.event.network.bungeecord.BungeeStartEvent;
import net.nevercloud.node.api.event.network.minecraftserver.ServerStartEvent;

import java.util.function.Consumer;

public class PacketInBungeeProcessStarted implements PacketHandler {
    @Override
    public void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResponse) {
        if (!(packet instanceof JsonPacket))
            return;

        BungeeCordProxyInfo proxyInfo = ((JsonPacket) packet).getSimpleJsonObject().getObject("proxyInfo", BungeeCordProxyInfo.class);
        NeverCloudNode.getInstance().getEventManager().callEvent(new BungeeStartEvent(proxyInfo));
    }
}
