package net.nevercloud.node.network.participants;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import lombok.Getter;
import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.auth.Auth;
import net.nevercloud.lib.server.BungeeCordProxyInfo;
import net.nevercloud.lib.server.MinecraftServerInfo;

import java.util.HashMap;
import java.util.Map;

@Getter
public class NodeParticipant extends NetworkParticipant {

    public NodeParticipant(Channel channel, Auth auth) {
        super(channel);
        this.auth = auth;
    }

    private Auth auth;

    private Map<String, MinecraftServerParticipant> servers = new HashMap<>();
    private Map<String, BungeeCordParticipant> proxies = new HashMap<>();

    private Map<String, MinecraftServerInfo> startingServers = new HashMap<>();
    private Map<String, BungeeCordProxyInfo> startingProxies = new HashMap<>();
    private Map<String, MinecraftServerInfo> waitingServers = new HashMap<>();
    private Map<String, BungeeCordProxyInfo> waitingProxies = new HashMap<>();


    public void startMinecraftServer(MinecraftServerInfo serverInfo) {
        Preconditions.checkArgument(serverInfo.getParentComponentName().equals(this.auth.getComponentName()), "serverInfo parent componentName is not equal with the name of the node to start on");
        this.waitingServers.put(serverInfo.getComponentName(), serverInfo);
    }

    public void startBungeeCordProxy(BungeeCordProxyInfo proxyInfo) {
        Preconditions.checkArgument(proxyInfo.getParentComponentName().equals(this.auth.getComponentName()), "proxyInfo parent componentName is not equal with the name of the node to start on");
        this.waitingProxies.put(proxyInfo.getComponentName(), proxyInfo);
    }

    public void closeConnection() {
        this.proxies.values().forEach(bungeeCordParticipant -> bungeeCordParticipant.getChannel().close());
        this.servers.values().forEach(minecraftServerParticipant -> minecraftServerParticipant.getChannel().close());
        this.proxies.clear();
        this.servers.clear();
        this.waitingProxies.clear();
        this.waitingServers.clear();
        this.startingServers.clear();
        this.startingProxies.clear();
        this.getChannel().close();
    }
}
