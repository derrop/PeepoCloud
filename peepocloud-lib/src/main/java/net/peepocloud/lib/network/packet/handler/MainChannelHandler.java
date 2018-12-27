package net.peepocloud.lib.network.packet.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.PacketInfo;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.utility.network.QueryRequest;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
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
        this.participant = new NetworkParticipant(null, channelHandlerContext.channel());
        this.channelHandler.connected(this.participant);
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
        this.channelHandler.disconnected(this.participant);
        this.participant = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause instanceof IOException)) {
            System.err.println("Exception with " + ctx.channel().remoteAddress() + ": " + cause.getClass().getSimpleName() + ", " + cause.getMessage());
            super.exceptionCaught(ctx, cause);
        }

        ctx.close();
        this.channelHandler.exception(this.participant, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if(this.channelHandler.packet(this.participant, packet))
            return;

        QueryRequest<Packet> query = this.packetManager.getQueryAndRemove(packet.getQueryUUID());
        if(query != null)
            query.setResponse(packet);

        PacketInfo packetInfo = this.packetManager.getPacketInfo(packet.getId());
        if(packetInfo != null) {
            packetInfo.getPacketHandler().handleInternal(this.participant, packet, (Consumer<Packet>) queryResponse -> {
                if (packet.getQueryUUID() != null)
                    this.participant.sendPacket(this.packetManager.convertToQueryPacket(queryResponse, packet.getQueryUUID()));
            });
        }
    }

    public void setParticipant(NetworkParticipant participant) {
        this.participant = participant;
    }

    public NetworkParticipant getParticipant() {
        return participant;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public void setChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }

}
