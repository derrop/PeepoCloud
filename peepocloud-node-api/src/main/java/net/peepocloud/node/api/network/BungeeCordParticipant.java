package net.peepocloud.node.api.network;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

public interface BungeeCordParticipant extends NetworkPacketSender {

    /**
     * Gets the {@link Auth} which was used for this participant to authenticate
     *
     * @return the {@link Auth} of this participant
     */
    Auth getAuth();

    /**
     * Gets the current {@link BungeeCordProxyInfo} of this participant
     *
     * @return the current {@link BungeeCordProxyInfo} of this participant
     */
    BungeeCordProxyInfo getProxyInfo();

    /**
     * Gets the last {@link BungeeCordProxyInfo} (the {@link BungeeCordProxyInfo} before the {@link #getProxyInfo()}) of this participant
     *
     * @return the last {@link BungeeCordProxyInfo} of this participant
     */
    BungeeCordProxyInfo getLastProxyInfo();

}
