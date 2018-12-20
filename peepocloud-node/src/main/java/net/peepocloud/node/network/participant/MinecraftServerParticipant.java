package net.peepocloud.node.network.participant;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

@Getter
public class MinecraftServerParticipant extends NetworkParticipant {
    private Auth auth;
    @Setter
    private MinecraftServerInfo serverInfo;

    public MinecraftServerParticipant(Channel channel, Auth auth) {
        super(auth.getComponentName(), channel);
        this.auth = auth;
        this.serverInfo = auth.getExtraData().getObject("serverInfo", MinecraftServerInfo.class);
    }


}
