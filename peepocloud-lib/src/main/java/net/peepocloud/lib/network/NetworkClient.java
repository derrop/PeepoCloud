package net.peepocloud.lib.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.network.auth.PacketOutAuth;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.network.packet.coding.PacketDecoder;
import net.peepocloud.lib.network.packet.coding.PacketEncoder;
import net.peepocloud.lib.network.packet.handler.ChannelHandler;
import net.peepocloud.lib.network.packet.handler.MainChannelHandler;
import net.peepocloud.lib.utility.network.QueryRequest;

import java.net.InetSocketAddress;

public class NetworkClient extends NetworkParticipant implements Runnable {
    private static final boolean EPOLL = Epoll.isAvailable();

    private InetSocketAddress address;
    private PacketManager packetManager;
    private ChannelHandler firstHandler;
    private Auth auth;

    private Runnable exceptionTask;

    public NetworkClient(InetSocketAddress address, PacketManager packetManager, ChannelHandler firstHandler, Auth auth) {
        super(auth.getComponentName(), null, -1);
        this.address = address;
        this.packetManager = packetManager;
        this.firstHandler = firstHandler;
        this.auth = auth;
    }

    @Override
    public void run() {
        System.out.println("&eTrying to connect to " + address);
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
                                    .addLast(new PacketDecoder(NetworkClient.this.packetManager))
                                    .addLast(new PacketEncoder())
                                    .addLast(new MainChannelHandler(NetworkClient.this.packetManager, NetworkClient.this.firstHandler));
                        }
                    });
            super.channel = bootstrap.connect(this.address).sync().channel().writeAndFlush(new PacketOutAuth(this.auth)).syncUninterruptibly().channel();
            super.connectedAt = System.currentTimeMillis();
            System.out.println("&aSuccessfully connected to " + address);
        } catch (Exception exception) {
            System.err.println("&eError while trying to connect to " + address + ": &e"+ exception.getMessage());
            eventLoopGroup.shutdownGracefully();
            if(this.exceptionTask != null)
                this.exceptionTask.run();
        }
    }

    public void shutdown() {
        if(super.isConnected())
            super.channel.close();
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public QueryRequest<Packet> packetQueryAsync(Packet packet) {
        return this.packetManager.packetQueryAsync(this, packet);
    }

    public Packet packetQuery(Packet packet) {
        return this.packetManager.packetQuery(this, packet);
    }

    public MainChannelHandler getPipelineHandler() {
        return super.channel.pipeline().get(MainChannelHandler.class);
    }

    public void setExceptionTask(Runnable exceptionTask) {
        this.exceptionTask = exceptionTask;
    }
}
