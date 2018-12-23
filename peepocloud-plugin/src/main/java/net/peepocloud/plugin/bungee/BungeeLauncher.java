package net.peepocloud.plugin.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLauncher extends Plugin  {
    private PeepoBungeePlugin bungeeAPI;

    @Override
    public void onEnable() {
        this.bungeeAPI = new PeepoBungeePlugin(this);
        this.bungeeAPI.registerNetworkHandler(new BungeeNetworkHandler(this.bungeeAPI));

        this.bungeeAPI.bootstrap();
    }

    @Override
    public void onDisable() {
        this.bungeeAPI.shutdown();
    }
}
