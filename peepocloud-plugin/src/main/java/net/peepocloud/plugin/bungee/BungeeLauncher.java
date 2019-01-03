package net.peepocloud.plugin.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.peepocloud.plugin.bungee.listener.BungeeListener;

public class BungeeLauncher extends Plugin {
    private PeepoBungeePlugin bungeeAPI;

    @Override
    public void onEnable() {
        this.bungeeAPI = new PeepoBungeePlugin(this);
        this.bungeeAPI.bootstrap();
    }

    @Override
    public void onDisable() {
        if(this.bungeeAPI != null)
            this.bungeeAPI.shutdown();
    }
}
