package net.nevercloud.node.network.participants;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import io.netty.channel.Channel;
import lombok.Getter;
import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.auth.Auth;
import net.nevercloud.lib.server.MinecraftServerInfo;

@Getter
public class MinecraftServerParticipant extends NetworkParticipant {
    private Auth auth;
    private NodeParticipant parent;
    private MinecraftServerInfo serverInfo;

    public MinecraftServerParticipant(Channel channel, Auth auth, NodeParticipant parent) {
        super(auth.getComponentName(), channel);
        this.auth = auth;
        this.parent = parent;
        this.serverInfo = auth.getExtraData().getObject("serverInfo", MinecraftServerInfo.class);
    }


}
