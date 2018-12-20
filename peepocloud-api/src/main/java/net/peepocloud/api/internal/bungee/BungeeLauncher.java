package net.peepocloud.api.internal.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLauncher extends Plugin  {
    private PeepoBungeeAPI bungeeAPI;

    @Override
    public void onEnable() {
        this.bungeeAPI = new PeepoBungeeAPI(this);
        this.bungeeAPI.bootstrap();
    }

    @Override
    public void onDisable() {
        this.bungeeAPI.shutdown();
    }
}
