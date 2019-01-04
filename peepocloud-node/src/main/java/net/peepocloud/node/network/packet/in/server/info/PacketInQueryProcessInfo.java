package net.peepocloud.node.network.packet.in.server.info;
/*
 * Created by Mc_Ruben on 04.01.2019
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.utility.network.PacketSerializableWrapper;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.network.NodeParticipant;
import net.peepocloud.node.network.packet.out.server.process.info.PacketOutQueryProcessInfo;
import oshi.software.os.OSProcess;

import java.util.function.Consumer;

public class PacketInQueryProcessInfo implements PacketHandler<PacketOutQueryProcessInfo> {
    @Override
    public int getId() {
        return 23;
    }

    @Override
    public Class<PacketOutQueryProcessInfo> getPacketClass() {
        return PacketOutQueryProcessInfo.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, PacketOutQueryProcessInfo packet, Consumer<Packet> queryResponse) {
        OSProcess process = PeepoCloudNode.getInstance().getSystemInfo().getOperatingSystem().getProcess(packet.getPid());
        queryResponse.accept(new SerializationPacket(99999, new PacketSerializableWrapper(process)));
    }

}
