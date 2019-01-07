package net.peepocloud.lib.network.packet.coding;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.peepocloud.lib.AbstractPeepoCloudAPI;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.PacketInfo;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.utility.network.PacketUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacketDecoder extends ByteToMessageDecoder {
    private PacketManager packetManager;

    public PacketDecoder(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(!(byteBuf instanceof EmptyByteBuf)) {
            byteBuf.resetReaderIndex();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);

            ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(bytes);
            int id = byteArrayDataInput.readInt();
            AbstractPeepoCloudAPI.getInstance().debug("Decoding packet " + id + "...");
            boolean isQuery = byteArrayDataInput.readBoolean();
            UUID queryUUID = isQuery ? PacketUtils.readUUID(byteArrayDataInput) : null;

            Class<? extends Packet> packetClass;
            if (isQuery && this.packetManager.hasQuery(queryUUID)) {
                Map.Entry<Class<? extends Packet>, Integer> entry =
                        this.packetManager.getQueryResponses().entrySet().stream().filter(entry1 -> entry1.getValue().equals(id)).findFirst().orElse(null);
                if (entry == null)
                    packetClass = null;
                else
                    packetClass = entry.getKey();
            } else {
                PacketInfo packetInfo = this.packetManager.getPacketInfo(id);
                if (packetInfo == null)
                    packetClass = null;
                else
                    packetClass = packetInfo.getPacketClass();
            }
            AbstractPeepoCloudAPI.getInstance().debug("Class for packet " + id + " (" + isQuery + ") is " + (packetClass == null ? "null" : packetClass.getName()));

            if (packetClass == null) {
                AbstractPeepoCloudAPI.getInstance().debug("Received invalid/unregistered packet from " + channelHandlerContext.channel().localAddress()
                        + " (" + id + "/" + queryUUID + ")");
                return;
            }

            Packet packet = packetClass.getDeclaredConstructor(int.class).newInstance(id);

            if(isQuery)
                this.packetManager.convertToQueryPacket(packet, queryUUID);

            AbstractPeepoCloudAPI.getInstance().debug("Received packet from " + channelHandlerContext.channel().localAddress()
                    + " (" + packet.getClass().getSimpleName() + "/" + packet.getId() + "/" + packet.getQueryUUID() + ")");

            packet.read(byteArrayDataInput);
            list.add(packet);
        }
    }
}
