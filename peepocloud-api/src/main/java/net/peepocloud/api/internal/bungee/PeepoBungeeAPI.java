package net.peepocloud.api.internal.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.peepocloud.api.internal.PeepoCloudAPI;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class PeepoBungeeAPI extends PeepoCloudAPI {
    private BungeeLauncher plugin;

    PeepoBungeeAPI(BungeeLauncher plugin) {
        super(Paths.get("nodeInfo.json"));
        this.plugin = plugin;
    }

    public void registerServerInfo(MinecraftServerInfo serverInfo) {
        ServerInfo newServer = this.plugin.getProxy().constructServerInfo(serverInfo.getComponentName(),
                new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort()), serverInfo.getMotd(), false);
        this.plugin.getProxy().getServers().put(serverInfo.getComponentName(), newServer);
    }

    public void unregisterServerInfo(MinecraftServerInfo serverInfo) {
        this.plugin.getProxy().getServers().remove(serverInfo.getComponentName());
    }

    @Override
    public boolean isBungee() {
        return true;
    }

    @Override
    public boolean isBukkit() {
        return false;
    }

    public BungeeLauncher getPlugin() {
        return plugin;
    }
}
