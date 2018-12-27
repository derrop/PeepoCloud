package net.peepocloud.plugin.network.packet.in.server;


import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.plugin.PeepoCloudPlugin;

import java.util.function.Consumer;

public class PacketInAPIServerStopped extends JsonPacketHandler {

    @Override
    public int getId() {
        return 101;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        if(packet.getSimpleJsonObject() == null || !packet.getSimpleJsonObject().contains("serverInfo"))
            return;
        MinecraftServerInfo serverInfo = packet.getSimpleJsonObject().getObject("serverInfo", MinecraftServerInfo.class);
        PeepoCloudPlugin.getInstance().getNetworkHandlers().forEach(handler -> handler.handleServerStop(serverInfo));
    }


}
