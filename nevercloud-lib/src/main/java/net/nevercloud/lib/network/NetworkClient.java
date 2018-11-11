package net.nevercloud.lib.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import net.nevercloud.lib.network.auth.Auth;
import net.nevercloud.lib.network.auth.PacketOutAuth;
import net.nevercloud.lib.network.packet.PacketManager;
import net.nevercloud.lib.network.packet.coding.PacketDecoder;
import net.nevercloud.lib.network.packet.coding.PacketEncoder;
import net.nevercloud.lib.network.packet.handler.ChannelHandler;
import net.nevercloud.lib.network.packet.handler.MainChannelHandler;

public class NetworkClient extends NetworkParticipant implements Runnable {
    private static final boolean EPOLL = Epoll.isAvailable();

    private String host;
    private int port;
    private PacketManager packetManager;
    private ChannelHandler firstHandler;
    private Auth auth;

    public NetworkClient(String host, int port, PacketManager packetManager, ChannelHandler firstHandler, Auth auth) {
        super(null, -1);
        this.host = host;
        this.port = port;
        this.packetManager = packetManager;
        this.firstHandler = firstHandler;
        this.auth = auth;
    }

    @Override
    public void run() {
        System.out.println("&eTrying to connect to " + this.host + ":" + this.port);
        EventLoopGroup eventLoopGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                    0, 4, 0, 4),
                                    new LengthFieldPrepender(4))
                                    .addLast(new PacketEncoder())
                                    .addLast(new PacketDecoder(NetworkClient.this.packetManager))
                                    .addLast(new MainChannelHandler(NetworkClient.this.packetManager, NetworkClient.this.firstHandler));
                        }
                    });
            super.channel = bootstrap.connect(this.host, this.port).syncUninterruptibly().channel().writeAndFlush(new PacketOutAuth(this.auth)).syncUninterruptibly().channel();
            super.connectedAt = System.currentTimeMillis();
            System.out.println("&aSuccessfully connected to " + this.host + ":" + this.port);
            super.channel.closeFuture().syncUninterruptibly();
        } catch (Exception exception) {
            System.err.println("&eError while trying to connect to " + this.host + ":" + this.port+": &e"+ exception.getMessage());
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void shutdown() {
        super.channel.close();
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public ChannelHandler getCurrentHandler() {
        return super.channel.pipeline().get(MainChannelHandler.class).getChannelHandler();
    }


}
