package net.peepocloud.plugin.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.plugin.api.bungee.PeepoCloudBungeeAPI;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class PeepoBungeePlugin extends PeepoCloudPlugin implements PeepoCloudBungeeAPI {
    private BungeeLauncher plugin;

    PeepoBungeePlugin(BungeeLauncher plugin) {
        super(Paths.get("nodeInfo.json"));
        this.plugin = plugin;
    }

    @Override
    public void registerServerInfo(MinecraftServerInfo serverInfo) {
        ServerInfo newServer = this.plugin.getProxy().constructServerInfo(serverInfo.getComponentName(),
                new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort()), serverInfo.getMotd(), false);
        this.plugin.getProxy().getServers().put(serverInfo.getComponentName(), newServer);
    }

    @Override
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

    @Override
    public BungeeLauncher getPlugin() {
        return plugin;
    }
}
