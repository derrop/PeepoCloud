package net.peepocloud.plugin.network.packet.in;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.plugin.PeepoCloudPlugin;

import java.util.function.Consumer;

public class PacketInServerInfo implements PacketHandler<SerializationPacket> {

    @Override
    public int getId() {
        return 9;
    }

    @Override
    public Class<SerializationPacket> getPacketClass() {
        return SerializationPacket.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, SerializationPacket packet, Consumer<Packet> queryResponse) {
        if (!(packet.getSerializable() instanceof MinecraftServerInfo))
            return;

        MinecraftServerInfo serverInfo = (MinecraftServerInfo) packet.getSerializable();
        if(PeepoCloudPlugin.getInstance().isBukkit())
            PeepoCloudPlugin.getInstance().toBukkit().updateCurrentServerInfo(serverInfo);
    }
}
