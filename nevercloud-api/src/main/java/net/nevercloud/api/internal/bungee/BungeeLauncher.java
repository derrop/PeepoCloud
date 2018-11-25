package net.nevercloud.api.internal.bungee;


import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLauncher extends Plugin  {
    private BungeeAPI bungeeAPI;

    @Override
    public void onEnable() {
        this.bungeeAPI = new BungeeAPI();
    }

    @Override
    public void onDisable() {
        this.bungeeAPI.shutdown();
    }
}
