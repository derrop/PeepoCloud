package net.peepocloud.lib.network.packet.coding;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import net.peepocloud.lib.network.packet.FilePacket;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.utility.network.PacketUtils;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();

        byteArrayDataOutput.writeInt(packet.getId());

        boolean isQuery = packet.getQueryUUID() != null;
        byteArrayDataOutput.writeBoolean(isQuery);

        if(isQuery)
            PacketUtils.writeUUID(byteArrayDataOutput, packet.getQueryUUID());



        packet.write(byteArrayDataOutput);
        byteBuf.writeBytes(byteArrayDataOutput.toByteArray());
    }
}
