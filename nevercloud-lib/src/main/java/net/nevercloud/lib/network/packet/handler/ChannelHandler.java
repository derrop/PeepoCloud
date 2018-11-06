package net.nevercloud.lib.network.packet.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.nevercloud.lib.network.packet.Packet;

public class ChannelHandler extends SimpleChannelInboundHandler<Packet> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {

    }
}
