package net.peepocloud.plugin.bukkit.listener;


import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.plugin.bukkit.PeepoBukkitPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

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
            // TODO cache Player
        } else {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cYou have to connect to a bungeecord-server first!"); // TODO: configurable
        }
    }


}
