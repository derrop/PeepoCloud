package net.nevercloud.lib.network.packet.coding;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.utility.PacketUtils;

public class PacketEncoder extends MessageToByteEncoder<Packet> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        byteBuf.writeInt(packet.getId());
        boolean isQuery = packet.getQueryUUID() != null;
        byteBuf.writeBoolean(isQuery);
        if(isQuery)
            PacketUtils.writeUUID(byteBuf, packet.getQueryUUID());

        packet.write(byteBuf);
    }
}
