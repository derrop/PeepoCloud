package net.peepocloud.api;


import net.peepocloud.api.internal.PeepoCloudAPIConfig;
import net.peepocloud.api.internal.bukkit.BukkitAPI;
import net.peepocloud.api.internal.bungee.BungeeAPI;

public class PeepoCloudAPI {
    private static PeepoCloudAPI instance;

    public PeepoCloudAPI(PeepoCloudAPIConfig peepoCloudAPIConfig) {
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

    public static PeepoCloudAPI getInstance() {
        return instance;
    }
}
