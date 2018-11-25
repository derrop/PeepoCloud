package net.nevercloud.api.internal.bukkit;


import org.bukkit.plugin.java.JavaPlugin;

public class BukkitLauncher extends JavaPlugin {
    private BukkitAPI bukkitAPI;

    @Override
    public void onEnable() {
        this.bukkitAPI = new BukkitAPI();
    }

    @Override
    public void onDisable() {
        this.bukkitAPI.shutdown();
    }
}
