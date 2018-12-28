package net.peepocloud.plugin.bukkit;

import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.api.bukkit.PeepoCloudBukkitAPI;
import net.peepocloud.plugin.bukkit.serverselector.signselector.SignSelector;

import java.nio.file.Paths;

public class PeepoBukkitPlugin extends PeepoCloudPlugin implements PeepoCloudBukkitAPI {
    private BukkitLauncher plugin;

    private SignSelector signSelector;

    PeepoBukkitPlugin(BukkitLauncher plugin) {
        super(Paths.get("nodeInfo.json"));
        this.plugin = plugin;
    }

    @Override
    public Runnable handleConnected() {
        return () -> {

        };
    }

    @Override
    public boolean isBungee() {
        return false;
    }


    @Override
    public boolean isBukkit() {
        return true;
    }

    public void enableSignSelector(SignSelector signSelector) {
        if(this.signSelector == null) {
            this.signSelector = signSelector;
            this.registerNetworkHandler(signSelector);
            signSelector.start(super.scheduler);
        }
    }

    @Override
    public BukkitLauncher getPlugin() {
        return plugin;
    }
}
