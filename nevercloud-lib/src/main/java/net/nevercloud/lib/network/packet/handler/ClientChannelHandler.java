package net.nevercloud.lib.network.packet.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.nevercloud.lib.network.NetworkClient;
import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.packet.Packet;

public class ClientChannelHandler extends SimpleChannelInboundHandler<Packet> {
    private NetworkClient networkClient;
    private NetworkParticipant server;

    public ClientChannelHandler(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        this.server = new NetworkParticipant(channelHandlerContext.channel());
        this.networkClient.getPacketHandler().onConnect(this.server);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.networkClient.getPacketHandler().onDisconnect(this.server);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.networkClient.getPacketHandler().onException(this.server, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        this.networkClient.getPacketHandler().handlePacket(this.server, packet, (queryResponse) -> this.networkClient.sendPacket(queryResponse));
    }
}
