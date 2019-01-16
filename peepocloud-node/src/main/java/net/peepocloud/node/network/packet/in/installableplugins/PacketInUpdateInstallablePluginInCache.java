package net.peepocloud.node.network.packet.in.installableplugins;
/*
 * Created by Mc_Ruben on 16.01.2019
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.network.packet.out.installableplugins.PacketOutUpdateInstallablePluginInCache;

import java.io.ByteArrayInputStream;
import java.util.function.Consumer;

public class PacketInUpdateInstallablePluginInCache implements PacketHandler<PacketOutUpdateInstallablePluginInCache> {
    @Override
    public int getId() {
        return 56;
    }

    @Override
    public Class<PacketOutUpdateInstallablePluginInCache> getPacketClass() {
        return PacketOutUpdateInstallablePluginInCache.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, PacketOutUpdateInstallablePluginInCache packet, Consumer<Packet> queryResponse) {
        PeepoCloudNode.getInstance().getPluginLoader().updatePlugin(packet.getPlugin(), new ByteArrayInputStream(packet.getData()));
    }
}
