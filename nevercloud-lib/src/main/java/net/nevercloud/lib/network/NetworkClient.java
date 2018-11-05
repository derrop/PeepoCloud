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
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.PacketRegister;
import net.nevercloud.lib.network.packet.coding.PacketDecoder;
import net.nevercloud.lib.network.packet.coding.PacketEncoder;

public class NetworkClient implements Runnable {
    private static final boolean EPOLL = Epoll.isAvailable();

    private Channel channel;
    private String host;
    private int port;
    private PacketRegister packetRegister;

    public NetworkClient(String host, int port, PacketRegister packetRegister) {
        this.host = host;
        this.port = port;
        this.packetRegister = packetRegister;
    }

    @Override
    public void run() {
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
                                    .addLast(new PacketDecoder(packetRegister));
                        }
                    });
            this.channel = bootstrap.connect(this.host, this.port).syncUninterruptibly().channel();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void shutdown() {
        this.channel.close();
    }

    public void sendPacket(Packet packet) {
        this.channel.writeAndFlush(packet);
    }

    public Channel getChannel() {
        return channel;
    }

    public PacketRegister getPacketRegister() {
        return packetRegister;
    }
}
