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

@AllArgsConstructor
public class PacketEncoder extends MessageToByteEncoder<Packet> {
    private PacketManager packetManager;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();

        if (packet.getQueryUUID() != null) {
            Class<? extends Packet> clazz = packet.getClass();
            if (!this.packetManager.getQueryResponses().containsKey(clazz)) {
                if (packet instanceof JsonPacket) {
                    clazz = JsonPacket.class;
                } else if (packet instanceof SerializationPacket) {
                    clazz = SerializationPacket.class;
                } else if (packet instanceof FilePacket) {
                    clazz = FilePacket.class;
                }
            }
            int id = this.packetManager.getQueryResponses().get(clazz);

            byteArrayDataOutput.writeInt(id);
            byteArrayDataOutput.writeBoolean(packet.getQueryUUID() != null);
            if (packet.getQueryUUID() != null)
                PacketUtils.writeUUID(byteArrayDataOutput, packet.getQueryUUID());
        } else {
            byteArrayDataOutput.writeInt(packet.getId());
        }


        packet.write(byteArrayDataOutput);
        byteBuf.writeBytes(byteArrayDataOutput.toByteArray());
    }
}
