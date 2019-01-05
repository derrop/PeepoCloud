package net.peepocloud.plugin.bukkit;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import lombok.AllArgsConstructor;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.plugin.api.network.handler.NetworkAPIHandler;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.users.User;
import net.peepocloud.plugin.bukkit.event.BukkitPluginChannelMessageEvent;

@AllArgsConstructor
public class BukkitNetworkHandler implements NetworkAPIHandler {

    private PeepoBukkitPlugin bukkitAPI;

    @Override
    public void handleServerAdd(MinecraftServerInfo serverInfo) {

    }

    @Override
    public void handleServerStop(MinecraftServerInfo serverInfo) {

    }

    @Override
    public void handleServerUpdate(MinecraftServerInfo oldInfo, MinecraftServerInfo newInfo) {
        if(this.bukkitAPI.getCurrentServerInfo() == null || newInfo.getComponentName().equalsIgnoreCase(this.bukkitAPI.getCurrentServerInfo().getComponentName()))
            this.bukkitAPI.updateCurrentServerInfo(newInfo);
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
    public void handleProxyUpdate(BungeeCordProxyInfo oldInfo, BungeeCordProxyInfo newInfo) {

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

    @Override
    public void handlePlayerLogin(PeepoPlayer player) {

    }

    @Override
    public void handlePlayerLogout(PeepoPlayer player) {

    }

    @Override
    public void handlePlayerUpdate(PeepoPlayer oldPlayer, PeepoPlayer newPlayer) {

    }

    @Override
    public void handlePluginChannelMessage(String senderComponent, String identifier, String message, SimpleJsonObject data) {
        this.bukkitAPI.getPlugin().getServer().getPluginManager().callEvent(new BukkitPluginChannelMessageEvent(senderComponent, identifier, message, data));
    }
}
