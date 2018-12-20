package net.peepocloud.api.internal.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.peepocloud.api.internal.NodeChildAPI;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import java.io.File;
import java.net.InetSocketAddress;

public class PeepoBungeeAPI extends NodeChildAPI {
    private BungeeLauncher plugin;

    PeepoBungeeAPI(BungeeLauncher plugin) {
        super(new File("nodeInfo.json"));
        this.plugin = plugin;
    }

    public void registerServerInfo(MinecraftServerInfo serverInfo) {
        ServerInfo newServer = this.plugin.getProxy().constructServerInfo(serverInfo.getComponentName(),
                new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort()), serverInfo.getMotd(), false);
        this.plugin.getProxy().getServers().put(serverInfo.getComponentName(), newServer);
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
