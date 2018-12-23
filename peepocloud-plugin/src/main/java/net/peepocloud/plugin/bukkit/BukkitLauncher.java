package net.peepocloud.plugin.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitLauncher extends JavaPlugin {
    private PeepoBukkitPlugin bukkitAPI;

    @Override
    public void onEnable() {
        this.bukkitAPI = new PeepoBukkitPlugin(this);
        this.bukkitAPI.bootstrap();
    }

    @Override
    public void onDisable() {
        this.bukkitAPI.shutdown();
    }
}
