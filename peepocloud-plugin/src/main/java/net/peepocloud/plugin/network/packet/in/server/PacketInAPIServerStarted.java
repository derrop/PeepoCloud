package net.peepocloud.plugin.network.packet.in.server;


import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;

import java.util.function.Consumer;

public class PacketInAPIServerStarted extends JsonPacketHandler {


    @Override
    public int getId() {
        return 100;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        if(packet.getSimpleJsonObject() == null || !packet.getSimpleJsonObject().contains("serverInfo"))
            return;
        MinecraftServerInfo serverInfo = packet.getSimpleJsonObject().getObject("serverInfo", MinecraftServerInfo.class);
        PeepoCloudPlugin.getInstance().getNetworkHandlers().forEach(handler -> handler.handleServerAdd(serverInfo));
    }
}
