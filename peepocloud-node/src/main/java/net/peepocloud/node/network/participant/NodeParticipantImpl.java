package net.peepocloud.node.network.participant;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.api.network.NodeParticipant;
import net.peepocloud.node.network.packet.out.node.PacketOutExecuteNodeCommand;
import net.peepocloud.node.network.packet.out.server.process.start.PacketOutStartBungee;
import net.peepocloud.node.network.packet.out.server.process.start.PacketOutStartServer;

import java.util.HashMap;
import java.util.Map;

@Getter
public class NodeParticipantImpl extends NetworkParticipant implements NodeParticipant {

    public NodeParticipantImpl(Channel channel, Auth auth) {
        super(auth.getComponentName(), channel);
        this.auth = auth;
    }

    private Auth auth;

    @Setter
    private NodeInfo nodeInfo;

    private Map<String, MinecraftServerInfo> servers = new HashMap<>();
    private Map<String, BungeeCordProxyInfo> proxies = new HashMap<>();

    private Map<String, MinecraftServerInfo> startingServers = new HashMap<>();
    private Map<String, BungeeCordProxyInfo> startingProxies = new HashMap<>();
    private Map<String, MinecraftServerInfo> waitingServers = new HashMap<>();
    private Map<String, BungeeCordProxyInfo> waitingProxies = new HashMap<>();


    public void startMinecraftServer(MinecraftServerInfo serverInfo) {
        Preconditions.checkArgument(serverInfo.getParentComponentName().equals(this.auth.getComponentName()), "serverInfo parent componentName is not equal with the name of the node to start on");
        this.waitingServers.put(serverInfo.getComponentName(), serverInfo);
        this.sendPacket(new PacketOutStartServer(serverInfo));
    }

    public void startBungeeCordProxy(BungeeCordProxyInfo proxyInfo) {
        Preconditions.checkArgument(proxyInfo.getParentComponentName().equals(this.auth.getComponentName()), "proxyInfo parent componentName is not equal with the name of the node to start on");
        this.waitingProxies.put(proxyInfo.getComponentName(), proxyInfo);
        this.sendPacket(new PacketOutStartBungee(proxyInfo));
    }

    public void closeConnection() {
        this.proxies.clear();
        this.servers.clear();
        this.waitingProxies.clear();
        this.waitingServers.clear();
        this.startingServers.clear();
        this.startingProxies.clear();
        this.getChannel().close();
    }

    @Override
    public void executeCommand(String command) {
        this.sendPacket(new PacketOutExecuteNodeCommand(command));
    }

}
