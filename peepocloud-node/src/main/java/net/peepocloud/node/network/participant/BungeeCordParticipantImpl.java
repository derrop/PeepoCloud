package net.peepocloud.node.network.participant;
/*
 * Created by Mc_Ruben on 11.11.2018
 */


import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.api.network.BungeeCordParticipant;

@Getter
public class BungeeCordParticipantImpl extends NetworkParticipant implements BungeeCordParticipant {
    private Auth auth;
    private BungeeCordProxyInfo proxyInfo;
    private BungeeCordProxyInfo lastProxyInfo;

    public BungeeCordParticipantImpl(Channel channel, Auth auth) {
        super(auth.getComponentName(), channel);
        this.auth = auth;
        this.proxyInfo = auth.getExtraData().getObject("proxyInfo", BungeeCordProxyInfo.class);
    }

    public void setProxyInfo(BungeeCordProxyInfo proxyInfo) {
        this.lastProxyInfo = this.proxyInfo;
        this.proxyInfo = proxyInfo;
    }
}
