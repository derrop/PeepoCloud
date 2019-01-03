package net.peepocloud.plugin.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.plugin.api.bungee.PeepoCloudBungeeAPI;
import net.peepocloud.plugin.network.packet.in.PacketInProxyInfo;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PeepoBungeePlugin extends PeepoCloudPlugin implements PeepoCloudBungeeAPI {
    private BungeeLauncher plugin;

    private BungeeCordProxyInfo currentProxyInfo;
    private Map<String, MinecraftServerInfo> cachedServers = new HashMap<>();

    PeepoBungeePlugin(BungeeLauncher plugin) {
        super(Paths.get("nodeInfo.json"));
        this.plugin = plugin;
    }

    @Override
    public void bootstrap() {
        super.getPacketManager().registerPacket(new PacketInProxyInfo());

        super.bootstrap();
        super.getMinecraftServers().complete(4, TimeUnit.SECONDS).forEach(this::registerServerInfo);
    }

    @Override
    public void registerServerInfo(MinecraftServerInfo serverInfo) {
        this.cachedServers.put(serverInfo.getComponentName(), serverInfo);

        if(!this.plugin.getProxy().getServers().containsKey(serverInfo.getComponentName())) {
            ServerInfo newServer = this.plugin.getProxy().constructServerInfo(serverInfo.getComponentName(),
                    new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort()), serverInfo.getMotd(), false);
            this.plugin.getProxy().getServers().put(serverInfo.getComponentName(), newServer);
        }
    }

    @Override
    public void unregisterServerInfo(MinecraftServerInfo serverInfo) {
        this.cachedServers.remove(serverInfo.getComponentName());
        this.plugin.getProxy().getServers().remove(serverInfo.getComponentName());
    }

    public Collection<MinecraftServerInfo> getCachedServers() {
        return this.cachedServers.values();
    }

    public Collection<MinecraftServerInfo> getCachedServers(String group) {
        return this.getCachedServers().stream().filter(serverInfo -> serverInfo.getGroupName().equalsIgnoreCase(group)).collect(Collectors.toList());
    }

    public MinecraftServerInfo getCachedServer(String name) {
        return this.cachedServers.get(name);
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
