package net.nevercloud.lib.network.packet.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.PacketManager;

import java.io.IOException;
import java.util.function.Consumer;

public class ClientChannelHandler extends SimpleChannelInboundHandler<Packet> {
    private NetworkParticipant server;
    private PacketManager packetManager;

    public ClientChannelHandler(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        this.server = new NetworkParticipant(channelHandlerContext.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.server = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(!(cause instanceof IOException)) {
            System.err.println("Error with " + ctx.channel().remoteAddress());
            cause.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        Consumer<Packet> query = this.packetManager.getQuery(packet.getQueryUUID());
        if(query != null)
            query.accept(packet);

        this.packetManager.getPacketInfo(packet.getId()).getPacketHandler().handlePacket(this.server, packet, queryResponse -> {
            if(packet.getQueryUUID() != null)
                this.server.sendPacket(this.packetManager.convertToQueryPacket(queryResponse, packet.getQueryUUID()));
        });
    }
}
