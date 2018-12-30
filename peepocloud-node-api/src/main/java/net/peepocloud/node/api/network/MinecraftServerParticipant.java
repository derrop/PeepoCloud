package net.peepocloud.node.api.network;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public interface MinecraftServerParticipant extends NetworkPacketSender {

    /**
     * Gets the {@link Auth} which was used for this participant to authenticate
     *
     * @return the {@link Auth} of this participant
     */
    Auth getAuth();

    /**
     * Gets the current {@link MinecraftServerInfo} of this participant
     *
     * @return the current {@link MinecraftServerInfo} of this participant
     */
    MinecraftServerInfo getServerInfo();

    /**
     * Gets the last {@link MinecraftServerInfo} (the {@link MinecraftServerInfo} before the {@link #getServerInfo()}) of this participant
     *
     * @return the last {@link MinecraftServerInfo} of this participant
     */
    MinecraftServerInfo getLastServerInfo();

}
