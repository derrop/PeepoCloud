package net.peepocloud.node.network.participant;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import io.netty.channel.Channel;
import lombok.Getter;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

@Getter
public class BungeeCordParticipant extends NetworkParticipant {
    private Auth auth;
    private BungeeCordProxyInfo proxyInfo;

    public BungeeCordParticipant(Channel channel, Auth auth) {
        super(auth.getComponentName(), channel);
        this.auth = auth;
        this.proxyInfo = auth.getExtraData().getObject("proxyInfo", BungeeCordProxyInfo.class);
    }



}
