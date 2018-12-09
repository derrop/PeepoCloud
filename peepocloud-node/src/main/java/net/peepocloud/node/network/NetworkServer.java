package net.peepocloud.node.network;
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
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.network.packet.PacketInfo;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.network.packet.coding.PacketDecoder;
import net.peepocloud.lib.network.packet.coding.PacketEncoder;
import net.peepocloud.lib.network.packet.handler.ChannelHandler;
import net.peepocloud.lib.network.packet.handler.MainChannelHandler;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.network.bungeecord.BungeeConnectEvent;
import net.peepocloud.node.api.event.network.minecraftserver.ServerConnectEvent;
import net.peepocloud.node.api.event.network.node.NodeConnectEvent;
import net.peepocloud.node.network.packet.auth.PacketInAuth;
import net.peepocloud.node.network.participant.BungeeCordParticipant;
import net.peepocloud.node.network.participant.MinecraftServerParticipant;
import net.peepocloud.node.network.participant.NodeParticipant;
import net.peepocloud.node.utility.NodeUtils;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class NetworkServer implements Runnable {

    private static final boolean EPOLL = Epoll.isAvailable();

    private InetSocketAddress address;
    @Getter
    private PacketManager packetManager;

    private Map<String, NodeParticipant> connectedNodes = new HashMap<>();
    @Getter
    private NodeParticipant coreNode;
    @Getter
    private InetSocketAddress serverHost;

    private ChannelHandler authHandler = new ServerAuthChannelHandler();
    private ChannelHandler defaultHandler = new ServerDefaultHandler();

    public NetworkServer(InetSocketAddress address, PacketManager packetManager) {
        this.address = address;
        this.packetManager = packetManager;
    }

    @Override
    public void run() {
        System.out.println("&eTrying to bind server @" + this.address);
        this.serverHost = address;
        this.packetManager.registerPacket(new PacketInfo(-1, PacketInAuth.class, new PacketInAuth.NetworkAuthHandler(this)));
        EventLoopGroup bossGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        EventLoopGroup workerGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        new ServerBootstrap()
                .channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .group(bossGroup, workerGroup)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                        0, 4, 0, 4),
                                new LengthFieldPrepender(4))
                                .addLast(new PacketDecoder(NetworkServer.this.packetManager))
                                .addLast(new PacketEncoder())
                                .addLast(new MainChannelHandler(NetworkServer.this.packetManager, NetworkServer.this.authHandler));
                    }
                })
                .bind(this.address);
        System.out.println("&aServer bound on @" + this.address);
    }

    public boolean isSelfNodeCore() {
        return this.coreNode == null;
    }

    public Map<String, NodeParticipant> getConnectedNodes() {
        return connectedNodes;
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

        if (auth.getAuthKey().equals(PeepoCloudNode.getInstance().getNetworkAuthKey())) {
            switch (auth.getType()) {
                case NODE: {
                    if (auth.getParentComponentName() != null)
                        break;

                    networkParticipant = new NodeParticipant(networkParticipant.getChannel(), auth);

                    this.connectedNodes.put(auth.getComponentName(), (NodeParticipant) networkParticipant);

                    PeepoCloudNode.getInstance().tryConnectToNode(networkParticipant.getAddress());
                    this.coreNode = (NodeParticipant) networkParticipant;
                    successful = true;

                    PeepoCloudNode.getInstance().getEventManager().callEvent(new NodeConnectEvent((NodeParticipant) networkParticipant));
                    NodeUtils.updateNodeInfoForSupport(null);
                }
                break;

                case MINECRAFT_SERVER: {
                    if (auth.getParentComponentName() == null)
                        break;

                    if (!auth.getParentComponentName().equals(PeepoCloudNode.getInstance().getNodeInfo().getName()))
                        break;

                    if (!PeepoCloudNode.getInstance().getProcessManager().getProcesses().containsKey(auth.getComponentName()))
                        break;

                    networkParticipant = new MinecraftServerParticipant(networkParticipant.getChannel(), auth);

                    PeepoCloudNode.getInstance().getServersOnThisNode().put(auth.getComponentName(), (MinecraftServerParticipant) networkParticipant);
                    successful = true;
                    PeepoCloudNode.getInstance().getEventManager().callEvent(new ServerConnectEvent((MinecraftServerParticipant) networkParticipant));

                }
                break;

                case BUNGEECORD: {
                    if (auth.getParentComponentName() == null)
                        break;

                    if (!auth.getParentComponentName().equals(PeepoCloudNode.getInstance().getNodeInfo().getName()))
                        break;

                    if (!PeepoCloudNode.getInstance().getProcessManager().getProcesses().containsKey(auth.getComponentName()))
                        break;

                    networkParticipant = new BungeeCordParticipant(networkParticipant.getChannel(), auth);

                    PeepoCloudNode.getInstance().getProxiesOnThisNode().put(auth.getComponentName(), (BungeeCordParticipant) networkParticipant);

                    successful = true;
                    PeepoCloudNode.getInstance().getEventManager().callEvent(new BungeeConnectEvent((BungeeCordParticipant) networkParticipant));

                }
                break;
            }
        }

        if (successful) {
            networkParticipant.getChannel().pipeline().get(MainChannelHandler.class).setChannelHandler(this.defaultHandler);
        } else {
            networkParticipant.getChannel().close();
        }
    }

}
