package net.peepocloud.plugin.bukkit;

import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.api.bukkit.PeepoCloudBukkitAPI;
import java.nio.file.Paths;

public class PeepoBukkitPlugin extends PeepoCloudPlugin implements PeepoCloudBukkitAPI {
    private BukkitLauncher plugin;

    PeepoBukkitPlugin(BukkitLauncher plugin) {
        super(Paths.get("nodeInfo.json"));
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

    @Override
    public BukkitLauncher getPlugin() {
        return plugin;
    }
}
