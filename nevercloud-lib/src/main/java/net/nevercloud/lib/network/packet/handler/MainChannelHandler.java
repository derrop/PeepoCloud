package net.nevercloud.lib.network.packet.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.PacketInfo;
import net.nevercloud.lib.network.packet.PacketManager;
import java.io.IOException;
import java.util.function.Consumer;

public class MainChannelHandler extends SimpleChannelInboundHandler<Packet> {
    private NetworkParticipant participant;
    private PacketManager packetManager;
    private ChannelHandler channelHandler;

    public MainChannelHandler(PacketManager packetManager, ChannelHandler firstHandler) {
        this.packetManager = packetManager;
        this.channelHandler = firstHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        this.participant = new NetworkParticipant(channelHandlerContext.channel());
        System.out.println("Participant connected (" + channelHandlerContext.channel().remoteAddress() + ")");
        this.channelHandler.connected(this.participant);
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
        System.out.println("Participant disconnected (" + channelHandlerContext.channel().remoteAddress() + ")");
        this.channelHandler.disconnected(this.participant);
        this.participant = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(!(cause instanceof IOException))
            System.err.println("Exception with " + ctx.channel().remoteAddress() + ": " + cause.getClass().getSimpleName() + ", " + cause.getMessage());

        ctx.close();
        this.channelHandler.exception(this.participant, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if(this.channelHandler.packet(this.participant, packet))
            return;

        Consumer<Packet> query = this.packetManager.getQueryAndRemove(packet.getQueryUUID());
        if(query != null)
            query.accept(packet);

        PacketInfo packetInfo = this.packetManager.getPacketInfo(packet.getId());
        if(packetInfo != null) {
            packetInfo.getPacketHandler().handlePacket(this.participant, packet, queryResponse -> {
                if (packet.getQueryUUID() != null)
                    this.participant.sendPacket(this.packetManager.convertToQueryPacket(queryResponse, packet.getQueryUUID()));
            });
        }
    }

    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }

    public void setChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }
}
