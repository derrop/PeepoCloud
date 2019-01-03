package net.peepocloud.node.network;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import com.google.gson.reflect.TypeToken;
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
import net.peepocloud.lib.network.packet.out.PacketOutToggleDebug;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.server.minecraft.MinecraftState;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.network.bungeecord.BungeeConnectEvent;
import net.peepocloud.node.api.event.network.minecraftserver.ServerConnectEvent;
import net.peepocloud.node.api.event.network.node.NodeConnectEvent;
import net.peepocloud.node.api.network.NodeParticipant;
import net.peepocloud.node.api.server.CloudProcess;
import net.peepocloud.node.network.packet.auth.PacketInAuth;
import net.peepocloud.node.network.packet.out.PacketOutProxyInfo;
import net.peepocloud.node.network.packet.out.PacketOutServerInfo;
import net.peepocloud.node.network.packet.out.server.connection.PacketOutBungeeConnected;
import net.peepocloud.node.network.packet.out.server.connection.PacketOutServerConnected;
import net.peepocloud.node.network.participant.BungeeCordParticipantImpl;
import net.peepocloud.node.network.participant.MinecraftServerParticipantImpl;
import net.peepocloud.node.network.participant.NodeParticipantImpl;
import net.peepocloud.node.utility.NodeUtils;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    private EventLoopGroup bossGroup, workerGroup;

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
        bossGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        workerGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        PeepoCloudNode.getInstance().getLogger().debug("Using " + (EPOLL ? "epoll transport" : "nio transport"));

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

    public void close() {
        if (this.bossGroup != null)
            this.bossGroup.shutdownGracefully();
        if (this.workerGroup != null)
            this.workerGroup.shutdownGracefully();
    }

    public boolean isSelfNodeCore() {
        return this.coreNode == null;
    }

    public Map<String, NodeParticipant> getConnectedNodes() {
        return connectedNodes;
    }

    public NodeParticipantImpl getConnectedNode(String id) {
        return (NodeParticipantImpl) this.connectedNodes.get(id);
    }

    public void closeNodeConnection(String host) {
        for (NodeParticipant value : this.connectedNodes.values()) {
            if (value.getAddress().equals(host)) {
                value.closeConnection();
                break;
            }
        }
    }

    public void handleNodeDisconnect(NodeParticipantImpl participant) {
        this.connectedNodes.remove(participant.getName());
        if (this.connectedNodes.isEmpty()) {
            this.coreNode = null;
        } else {
            NodeParticipant newCore = null;
            for (NodeParticipant value : this.connectedNodes.values()) {
                if (newCore == null || value.getConnectedAt() < newCore.getConnectedAt()) {
                    newCore = value;
                }
            }
            if (newCore == null) {
                this.coreNode = null;
                return;
            }
            if (PeepoCloudNode.getInstance().getStartupTime() < newCore.getConnectedAt()) {
                this.coreNode = null;
            } else {
                this.coreNode = newCore;
            }
        }

    }

    public void handleAuth(NetworkParticipant networkParticipant, Auth auth) {
        boolean successful = false;

        if (auth.getAuthKey().equals(PeepoCloudNode.getInstance().getNetworkAuthKey())) {
            switch (auth.getType()) {
                case NODE: {
                    if (auth.getParentComponentName() != null || networkParticipant.getChannel() == null)
                        break;

                    if (!auth.getExtraData().contains("nodeInfo"))
                        break;

                    NodeInfo nodeInfo = auth.getExtraData().getObject("nodeInfo", NodeInfo.class);

                    if (nodeInfo == null || nodeInfo.getName() == null)
                        break;

                    boolean accept = false;
                    for (ConnectableNode connectableNode : PeepoCloudNode.getInstance().getCloudConfig().getConnectableNodes()) {
                        if (connectableNode.getName().equals(nodeInfo.getName()) && connectableNode.getAddress().getHost().equals(networkParticipant.getAddress())) {
                            accept = true;
                        }
                    }
                    if (!accept)
                        break;

                    networkParticipant = new NodeParticipantImpl(networkParticipant.getChannel(), auth);

                    if (auth.getExtraData().contains("servers")) {
                        ((NodeParticipantImpl) networkParticipant).getServers().putAll(
                                ((Collection<MinecraftServerInfo>) auth.getExtraData().getObject("servers", new TypeToken<Collection<MinecraftServerInfo>>() {
                                }.getType())).stream().collect(Collectors.toMap(MinecraftServerInfo::getComponentName, o -> o))
                        );
                    }
                    if (auth.getExtraData().contains("startingServers")) {
                        ((NodeParticipantImpl) networkParticipant).getStartingServers().putAll(
                                ((Collection<MinecraftServerInfo>) auth.getExtraData().getObject("startingServers", new TypeToken<Collection<MinecraftServerInfo>>() {
                                }.getType())).stream().collect(Collectors.toMap(MinecraftServerInfo::getComponentName, o -> o))
                        );
                    }
                    if (auth.getExtraData().contains("queuedServers")) {
                        ((NodeParticipantImpl) networkParticipant).getWaitingServers().putAll(
                                ((Collection<MinecraftServerInfo>) auth.getExtraData().getObject("queuedServers", new TypeToken<Collection<MinecraftServerInfo>>() {
                                }.getType())).stream().collect(Collectors.toMap(MinecraftServerInfo::getComponentName, o -> o))
                        );
                    }

                    if (auth.getExtraData().contains("proxies")) {
                        ((NodeParticipantImpl) networkParticipant).getProxies().putAll(
                                ((Collection<BungeeCordProxyInfo>) auth.getExtraData().getObject("proxies", new TypeToken<Collection<BungeeCordProxyInfo>>() {
                                }.getType())).stream().collect(Collectors.toMap(BungeeCordProxyInfo::getComponentName, o -> o))
                        );
                    }
                    if (auth.getExtraData().contains("startingProxies")) {
                        ((NodeParticipantImpl) networkParticipant).getStartingProxies().putAll(
                                ((Collection<BungeeCordProxyInfo>) auth.getExtraData().getObject("startingProxies", new TypeToken<Collection<BungeeCordProxyInfo>>() {
                                }.getType())).stream().collect(Collectors.toMap(BungeeCordProxyInfo::getComponentName, o -> o))
                        );
                    }
                    if (auth.getExtraData().contains("queuedProxies")) {
                        ((NodeParticipantImpl) networkParticipant).getWaitingProxies().putAll(
                                ((Collection<BungeeCordProxyInfo>) auth.getExtraData().getObject("queuedProxies", new TypeToken<Collection<BungeeCordProxyInfo>>() {
                                }.getType())).stream().collect(Collectors.toMap(BungeeCordProxyInfo::getComponentName, o -> o))
                        );
                    }
                    this.connectedNodes.put(auth.getComponentName(), (NodeParticipantImpl) networkParticipant);

                    String address = networkParticipant.getAddress();
                    PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
                        SystemUtils.sleepUninterruptedly(1500);
                        PeepoCloudNode.getInstance().tryConnectToNode(address);
                    });
                    boolean core = true;
                    for (ClientNodeImpl clientNode : PeepoCloudNode.getInstance().getConnectedNodes().values()) {
                        if (clientNode.getAddress() != null && clientNode.getAddress().equals(address)) {
                            core = false;
                        }
                    }
                    if (core) {
                        this.coreNode = (NodeParticipantImpl) networkParticipant;
                    }

                    PeepoCloudNode.getInstance().getServerNodes().put(networkParticipant.getName(), (NodeParticipantImpl) networkParticipant);

                    successful = true;
                    System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("network.connect.node")
                            .replace("%name%", nodeInfo.getName()).replace("%memory%", Integer.toString(nodeInfo.getMaxMemory()))
                            .replace("%cpu%", Integer.toString(nodeInfo.getCpuCores()))
                    );

                    PeepoCloudNode.getInstance().getEventManager().callEvent(new NodeConnectEvent((NodeParticipantImpl) networkParticipant));
                    NodeUtils.updateNodeInfoForSupport(null);
                }
                break;

                case MINECRAFT_SERVER: {
                    if (auth.getParentComponentName() == null || networkParticipant.getChannel() == null)
                        break;

                    if (!auth.getParentComponentName().equals(PeepoCloudNode.getInstance().getNodeInfo().getName()))
                        break;

                    CloudProcess process = PeepoCloudNode.getInstance().getProcessManager().getProcesses().get(auth.getComponentName());

                    if (process == null || !process.isServer())
                        break;

                    networkParticipant = new MinecraftServerParticipantImpl(networkParticipant.getChannel(), auth);

                    ((MinecraftServerParticipantImpl) networkParticipant).setServerInfo(process.getServerInfo());
                    process.getServerInfo().setState(MinecraftState.LOBBY);
                    PeepoCloudNode.getInstance().updateServerInfo(process.getServerInfo());

                    PeepoCloudNode.getInstance().getServersOnThisNode().put(auth.getComponentName(), (MinecraftServerParticipantImpl) networkParticipant);
                    PeepoCloudNode.getInstance().sendPacketToNodes(new PacketOutServerConnected(process.getServerInfo()));

                    successful = true;

                    System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("network.connect.server")
                            .replace("%name%", process.getName()).replace("%memory%", Integer.toString(process.getMemory()))
                    );

                    networkParticipant.sendPacket(new PacketOutServerInfo(process.getServerInfo()));

                    PeepoCloudNode.getInstance().getEventManager().callEvent(new ServerConnectEvent((MinecraftServerParticipantImpl) networkParticipant));

                }
                break;

                case BUNGEECORD: {
                    if (auth.getParentComponentName() == null || networkParticipant.getChannel() == null)
                        break;

                    if (!auth.getParentComponentName().equals(PeepoCloudNode.getInstance().getNodeInfo().getName()))
                        break;

                    CloudProcess process = PeepoCloudNode.getInstance().getProcessManager().getProcesses().get(auth.getComponentName());

                    if (process == null || !process.isProxy())
                        break;

                    networkParticipant = new BungeeCordParticipantImpl(networkParticipant.getChannel(), auth);

                    ((BungeeCordParticipantImpl) networkParticipant).setProxyInfo(process.getProxyInfo());

                    PeepoCloudNode.getInstance().getProxiesOnThisNode().put(auth.getComponentName(), (BungeeCordParticipantImpl) networkParticipant);
                    PeepoCloudNode.getInstance().sendPacketToNodes(new PacketOutBungeeConnected(process.getProxyInfo()));

                    successful = true;
                    System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("network.connect.bungee")
                            .replace("%name%", process.getName()).replace("%memory%", Integer.toString(process.getMemory()))
                    );

                    networkParticipant.sendPacket(new PacketOutProxyInfo(process.getProxyInfo()));

                    PeepoCloudNode.getInstance().getEventManager().callEvent(new BungeeConnectEvent((BungeeCordParticipantImpl) networkParticipant));

                }
                break;
            }
        }
        if (successful) {
            MainChannelHandler channelHandler = networkParticipant.getChannel().pipeline().get(MainChannelHandler.class);
            channelHandler.setChannelHandler(this.defaultHandler);
            channelHandler.setParticipant(networkParticipant);

            if (PeepoCloudNode.getInstance().getLogger().isDebugging()) {
                networkParticipant.sendPacket(new PacketOutToggleDebug(true));
            }
        } else {
            networkParticipant.getChannel().close();
        }
    }

}
