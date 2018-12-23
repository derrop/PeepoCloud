package net.peepocloud.plugin.api.network.handler;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.users.User;

public interface NetworkAPIHandler {

    void handleServerAdd(MinecraftServerInfo serverInfo);

    void handleServerStop(MinecraftServerInfo serverInfo);

    void handleServerQueued(MinecraftServerInfo serverInfo);

    void handleProxyAdd(BungeeCordProxyInfo proxyInfo);

    void handleProxyStop(BungeeCordProxyInfo proxyInfo);

    void handleProxyQueued(BungeeCordProxyInfo proxyInfo);

    void handleUserAdd(User user);

    void handleUserRemove(User user);

    void handleUserUpdate(User oldUser, User newUser);


}
