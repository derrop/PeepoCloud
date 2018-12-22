package net.peepocloud.api.internal.bukkit;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import lombok.AllArgsConstructor;
import net.peepocloud.api.internal.network.NetAPIHandler;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.users.User;

@AllArgsConstructor
public class BukkitNetHandler implements NetAPIHandler {

    private PeepoBukkitAPI bukkitAPI;

    @Override
    public void handleServerAdd(MinecraftServerInfo serverInfo) {

    }

    @Override
    public void handleServerStop(MinecraftServerInfo serverInfo) {

    }

    @Override
    public void handleServerQueued(MinecraftServerInfo serverInfo) {

    }

    @Override
    public void handleProxyAdd(BungeeCordProxyInfo proxyInfo) {

    }

    @Override
    public void handleProxyStop(BungeeCordProxyInfo proxyInfo) {

    }

    @Override
    public void handleProxyQueued(BungeeCordProxyInfo proxyInfo) {

    }

    @Override
    public void handleUserAdd(User user) {

    }

    @Override
    public void handleUserRemove(User user) {

    }

    @Override
    public void handleUserUpdate(User oldUser, User newUser) {

    }
}
