package net.peepocloud.plugin.bukkit.listener;


import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.bukkit.PeepoBukkitPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitPlayerListener implements Listener {

    private PeepoBukkitPlugin bukkitAPI;

    public BukkitPlayerListener(PeepoBukkitPlugin bukkitAPI) {
        this.bukkitAPI = bukkitAPI;
    }

    @EventHandler
    public void handlePreLogin(AsyncPlayerPreLoginEvent event) {
        PeepoPlayer peepoPlayer = this.bukkitAPI.getPlayer(event.getUniqueId()).complete();
        if(peepoPlayer != null) {
            // TODO check if player tries to join the server from bungeeCord

            PeepoCloudPlugin.getInstance().getCachedPlayers().put(peepoPlayer.getUniqueId(), peepoPlayer);
        } else {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cYou have to connect to a bungeecord-server first!"); // TODO: configurable
        }
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        PeepoCloudPlugin.getInstance().getCachedPlayers().remove(event.getPlayer().getUniqueId());
    }


}
