package net.nevercloud.lib.network.packet.coding;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.PacketRegister;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    private PacketRegister packetRegister;

    public PacketDecoder(PacketRegister packetRegister) {
        this.packetRegister = packetRegister;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if(byteBuf.isReadable()) {
            int id = byteBuf.readInt();
            Class<? extends Packet> packetClass = this.packetRegister.getPacket(id);
            try {
                Packet packet = packetClass.getDeclaredConstructor(Integer.class).newInstance(id);
                packet.read(byteBuf);
                list.add(packet);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
