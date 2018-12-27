package net.peepocloud.node.api.network;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public interface MinecraftServerParticipant extends NetworkPacketSender {

    Auth getAuth();

    MinecraftServerInfo getServerInfo();

    MinecraftServerInfo getLastServerInfo();

}
