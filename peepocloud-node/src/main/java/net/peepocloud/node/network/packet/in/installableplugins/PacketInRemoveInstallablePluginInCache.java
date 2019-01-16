package net.peepocloud.node.network.packet.in.installableplugins;
/*
 * Created by Mc_Ruben on 16.01.2019
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.network.packet.out.installableplugins.PacketOutRemoveInstallablePluginInCache;
import net.peepocloud.node.network.packet.out.installableplugins.PacketOutUpdateInstallablePluginInCache;

import java.io.ByteArrayInputStream;
import java.util.function.Consumer;

public class PacketInRemoveInstallablePluginInCache implements PacketHandler<PacketOutRemoveInstallablePluginInCache> {
    @Override
    public int getId() {
        return 57;
    }

    @Override
    public Class<PacketOutRemoveInstallablePluginInCache> getPacketClass() {
        return PacketOutRemoveInstallablePluginInCache.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, PacketOutRemoveInstallablePluginInCache packet, Consumer<Packet> queryResponse) {
        PeepoCloudNode.getInstance().getPluginLoader().unloadPlugin(packet.getName());
    }
}
