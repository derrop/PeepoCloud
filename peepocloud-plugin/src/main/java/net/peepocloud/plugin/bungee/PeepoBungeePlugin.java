package net.peepocloud.plugin.bungee;

import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.plugin.api.bungee.PeepoCloudBungeeAPI;
import net.peepocloud.plugin.network.packet.in.PacketInProxyInfo;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class PeepoBungeePlugin extends PeepoCloudPlugin implements PeepoCloudBungeeAPI {
    private BungeeLauncher plugin;

    private BungeeCordProxyInfo currentProxyInfo;

    PeepoBungeePlugin(BungeeLauncher plugin) {
        super(Paths.get("nodeInfo.json"));
        this.plugin = plugin;

        super.getPacketManager().registerPacket(new PacketInProxyInfo());
    }

    @Override
    public void registerServerInfo(MinecraftServerInfo serverInfo) {
        if(!this.plugin.getProxy().getServers().containsKey(serverInfo.getComponentName())) {
            ServerInfo newServer = this.plugin.getProxy().constructServerInfo(serverInfo.getComponentName(),
                    new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort()), serverInfo.getMotd(), false);
            this.plugin.getProxy().getServers().put(serverInfo.getComponentName(), newServer);
        }
    }

    @Override
    public void unregisterServerInfo(MinecraftServerInfo serverInfo) {
        this.plugin.getProxy().getServers().remove(serverInfo.getComponentName());
    }

    @Override
    public Runnable handleConnected() {
        return () -> super.getMinecraftServers().complete().forEach(this::registerServerInfo);
    }

    @Override
    public boolean isBungee() {
        return true;
    }

    @Override
    public boolean isBukkit() {
        return false;
    }

    public void updateCurrentProxyInfo(BungeeCordProxyInfo proxyInfo) {
        this.currentProxyInfo = proxyInfo;
    }

    public BungeeCordProxyInfo getCurrentProxyInfo() {
        return currentProxyInfo;
    }

    @Override
    public BungeeLauncher getPlugin() {
        return plugin;
    }
}
