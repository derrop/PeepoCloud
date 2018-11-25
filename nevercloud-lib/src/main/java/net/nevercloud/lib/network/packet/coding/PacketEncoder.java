package net.nevercloud.lib.network.packet.coding;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.utility.network.PacketUtils;

public class PacketEncoder extends MessageToByteEncoder<Packet> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeInt(packet.getId());
        byteArrayDataOutput.writeBoolean(packet.getQueryUUID() != null);
        if (packet.getQueryUUID() != null)
            PacketUtils.writeUUID(byteArrayDataOutput, packet.getQueryUUID());

        ByteBuf buf = Unpooled.buffer();
        packet.write(buf);
        byte[] bytes = buf.array();
        byteArrayDataOutput.writeInt(bytes.length);
        for (byte aByte : bytes) {
            byteArrayDataOutput.writeByte(aByte);
        }
        byteBuf.writeBytes(byteArrayDataOutput.toByteArray());
    }
}
