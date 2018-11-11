package net.nevercloud.node.network;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;
import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.auth.Auth;
import net.nevercloud.lib.network.packet.PacketInfo;
import net.nevercloud.lib.network.packet.PacketManager;
import net.nevercloud.lib.network.packet.coding.PacketDecoder;
import net.nevercloud.lib.network.packet.coding.PacketEncoder;
import net.nevercloud.lib.network.packet.handler.ChannelHandler;
import net.nevercloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.nevercloud.lib.network.packet.handler.MainChannelHandler;
import net.nevercloud.lib.utility.NetworkAddress;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.network.packet.serverside.auth.PacketInAuth;
import net.nevercloud.node.network.participants.*;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class NetworkServer {

    private static final boolean EPOLL = Epoll.isAvailable();

    private EventLoopGroup bossGroup, workerGroup;

    @Getter
    private PacketManager packetManager;

    private Map<String, NodeParticipant> connectedNodes = new HashMap<>();
    @Getter
    private NodeParticipant coreNode;
    @Getter
    private InetSocketAddress serverHost;

    private ChannelHandler authHandler = new ServerAuthChannelHandler();
    private ChannelHandler defaultHandler = new ChannelHandlerAdapter();

    public NetworkServer(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    public void start(InetSocketAddress address) {
        System.out.println("&eTrying to bind server @" + address.getHostString() + ":" + address.getPort() + "...");
        this.serverHost = address;
        this.packetManager.registerPacket(new PacketInfo(-1, PacketInAuth.class, new PacketInAuth.NetworkAuthHandler(this)));
        this.bossGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        this.workerGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        new ServerBootstrap()
                .channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .group(this.bossGroup, this.workerGroup)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                        0, 4, 0, 4),
                                new LengthFieldPrepender(4))
                                .addLast(new PacketEncoder())
                                .addLast(new PacketDecoder(packetManager))
                                .addLast("authHandler", new MainChannelHandler(packetManager, authHandler));
                    }
                })
                .bind(address);
        System.out.println("&aServer bound on @" + address.getHostString() + ":" + address.getPort());
    }

    public boolean isSelfNodeCore() {
        return this.coreNode == null;
    }

    public NodeParticipant getConnectedNode(String id) {
        return this.connectedNodes.get(id);
    }

    public void closeNodeConnection(String host) {
        for (NodeParticipant value : this.connectedNodes.values()) {
            if (value.getAddress().equals(host)) {
                value.closeConnection();
                break;
            }
        }
    }

    public void handleAuth(NetworkParticipant networkParticipant, Auth auth) {
        boolean successful = false;

        if (auth.getAuthKey().equals(NeverCloudNode.getInstance().getNetworkAuthKey())) {
            switch (auth.getType()) {
                case NODE:{
                    if (auth.getParentComponentName() != null)
                        break;

                    networkParticipant = new NodeParticipant(networkParticipant.getChannel(), auth);

                    this.connectedNodes.put(auth.getComponentName(), (NodeParticipant) networkParticipant);

                    NeverCloudNode.getInstance().tryConnectToNode(networkParticipant.getAddress());
                    successful = true;
                }
                break;

                case MINECRAFT_SERVER:{
                    if (auth.getParentComponentName() == null)
                        break;

                    NodeParticipant parent = this.getConnectedNode(auth.getParentComponentName());
                    if (parent == null)
                        break;

                    if (!parent.getStartingServers().containsKey(auth.getComponentName()))
                        break;

                    networkParticipant = new MinecraftServerParticipant(networkParticipant.getChannel(), auth, parent);

                    parent.getStartingServers().remove(auth.getComponentName());
                    parent.getServers().put(auth.getComponentName(), (MinecraftServerParticipant) networkParticipant);
                    successful = true;

                }
                break;

                case BUNGEECORD:{
                    if (auth.getParentComponentName() == null)
                        break;

                    NodeParticipant parent = this.getConnectedNode(auth.getParentComponentName());
                    if (parent == null)
                        break;

                    if (!parent.getStartingProxies().containsKey(auth.getComponentName()))
                        break;

                    networkParticipant = new BungeeCordParticipant(networkParticipant.getChannel(), auth, parent);

                    parent.getStartingProxies().remove(auth.getComponentName());
                    parent.getProxies().put(auth.getComponentName(), (BungeeCordParticipant) networkParticipant);
                    successful = true;

                }
                break;
            }
        }

        if (successful) {
            ((MainChannelHandler) networkParticipant.getChannel().pipeline().get("authHandler")).setChannelHandler(this.defaultHandler);
        } else {
            networkParticipant.getChannel().close();
        }
    }

}
