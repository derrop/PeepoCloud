package net.peepocloud.plugin.network.packet.in;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.api.network.handler.NetworkAPIHandler;

import java.util.function.Consumer;

public class PacketInUpdateServerInfo extends JsonPacketHandler {

    @Override
    public int getId() {
        return 9;
    }


    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        SimpleJsonObject simpleJsonObject = packet.getSimpleJsonObject();
        if (simpleJsonObject == null)
            return;

        MinecraftServerInfo oldInfo = simpleJsonObject.getObject("oldInfo", MinecraftServerInfo.class);
        MinecraftServerInfo newInfo = simpleJsonObject.getObject("newInfo", MinecraftServerInfo.class);

        PeepoCloudPlugin.getInstance().getNetworkHandlers().forEach(networkAPIHandler ->
                networkAPIHandler.handleServerUpdate(oldInfo, newInfo));
    }
}
