package net.peepocloud.node.api.network;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

public interface BungeeCordParticipant extends NetworkPacketSender {

    Auth getAuth();

    BungeeCordProxyInfo getProxyInfo();

    BungeeCordProxyInfo getLastProxyInfo();

}
