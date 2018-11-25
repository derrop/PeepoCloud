package net.nevercloud.node.network.participant;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import io.netty.channel.Channel;
import lombok.Getter;
import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.auth.Auth;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;

@Getter
public class BungeeCordParticipant extends NetworkParticipant {
    private Auth auth;
    private NodeParticipant parent;
    private BungeeCordProxyInfo proxyInfo;

    public BungeeCordParticipant(Channel channel, Auth auth, NodeParticipant parent) {
        super(auth.getComponentName(), channel);
        this.auth = auth;
        this.parent = parent;
        this.proxyInfo = auth.getExtraData().getObject("proxyInfo", BungeeCordProxyInfo.class);
    }



}
