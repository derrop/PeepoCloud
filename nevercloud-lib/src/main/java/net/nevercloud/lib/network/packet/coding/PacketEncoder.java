package net.nevercloud.lib.network.packet.coding;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.nevercloud.lib.network.packet.Packet;

public class PacketEncoder extends MessageToByteEncoder<Packet> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        byteBuf.writeInt(packet.getId());
        packet.write(byteBuf);
    }
}
