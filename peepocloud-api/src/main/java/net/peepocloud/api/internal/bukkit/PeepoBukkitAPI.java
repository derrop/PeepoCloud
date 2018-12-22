package net.peepocloud.api.internal.bukkit;

import net.peepocloud.api.internal.PeepoCloudAPI;

import java.io.File;

public class PeepoBukkitAPI extends PeepoCloudAPI {
    private BukkitLauncher plugin;

    PeepoBukkitAPI(BukkitLauncher plugin) {
        super(new File("nodeInfo.json"));
        this.plugin = plugin;
    }

    @Override
    public boolean isBungee() {
        return false;
    }

    @Override
    public boolean isBukkit() {
        return true;
    }

    public BukkitLauncher getPlugin() {
        return plugin;
    }
}
