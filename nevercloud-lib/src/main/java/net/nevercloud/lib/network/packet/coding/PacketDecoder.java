package net.nevercloud.lib.network.packet.coding;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.PacketManager;
import net.nevercloud.lib.utility.PacketUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    private PacketManager packetManager;

    public PacketDecoder(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if(byteBuf.isReadable()) {
            int id = byteBuf.readInt();
            boolean isQuery = byteBuf.readBoolean();

            Class<? extends Packet> packetClass = this.packetManager.getPacketInfo(id).getPacketClass();
            try {
                Packet packet = packetClass.getDeclaredConstructor(Integer.class).newInstance(id);

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
