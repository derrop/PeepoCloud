package net.peepocloud.api.internal.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitLauncher extends JavaPlugin {
    private PeepoBukkitAPI bukkitAPI;

    @Override
    public void onEnable() {
        this.bukkitAPI = new PeepoBukkitAPI(this);
        this.bukkitAPI.bootstrap();
    }

    @Override
    public void onDisable() {
        this.bukkitAPI.shutdown();
    }
}
