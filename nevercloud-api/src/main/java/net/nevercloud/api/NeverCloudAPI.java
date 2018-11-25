package net.nevercloud.api;


import net.nevercloud.api.internal.NeverCloudAPIConfig;
import net.nevercloud.api.internal.bukkit.BukkitAPI;
import net.nevercloud.api.internal.bungee.BungeeAPI;

public class NeverCloudAPI {
    private static NeverCloudAPI instance;

    public NeverCloudAPI(NeverCloudAPIConfig neverCloudAPIConfig) {
        instance = this;
    }

    public void shutdown() {

    }

    public BukkitAPI bukkit() {
        if(this instanceof BukkitAPI)
            return (BukkitAPI) this;
        else
            throw new UnsupportedOperationException("Cannot access bukkit-api from BungeeCord");
    }

    public BungeeAPI bungee() {
        if(this instanceof BungeeAPI)
            return (BungeeAPI) this;
        else
            throw new UnsupportedOperationException("Cannot access bungee-api from Bukkit");
    }

    public static NeverCloudAPI getInstance() {
        return instance;
    }
}
