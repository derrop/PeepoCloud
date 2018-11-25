package net.nevercloud.lib.network.packet.coding;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.PacketInfo;
import net.nevercloud.lib.network.packet.PacketManager;
import net.nevercloud.lib.utility.network.PacketUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    private PacketManager packetManager;

    public PacketDecoder(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if(!(byteBuf instanceof EmptyByteBuf)) {
            byteBuf.resetReaderIndex();

            int id = PacketUtils.readVarInt(byteBuf);
            boolean isQuery = byteBuf.readBoolean();

            PacketInfo packetInfo = this.packetManager.getPacketInfo(id);
            if (packetInfo == null)
                return;
            Class<? extends Packet> packetClass = packetInfo.getPacketClass();
            try {
                Packet packet = packetClass.getDeclaredConstructor(int.class).newInstance(id);

                if(isQuery)
                    this.packetManager.convertToQueryPacket(packet, PacketUtils.readUUID(byteBuf));

                packet.read(byteBuf);
                list.add(packet);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
