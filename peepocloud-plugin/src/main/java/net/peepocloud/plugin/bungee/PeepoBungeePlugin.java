package net.peepocloud.plugin.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.plugin.api.bungee.PeepoCloudBungeeAPI;
import net.peepocloud.plugin.bungee.listener.BungeePlayerListener;
import net.peepocloud.plugin.bungee.listener.BungeePluginChannelMessageListener;
import net.peepocloud.plugin.network.packet.in.PacketInUpdateProxyInfo;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PeepoBungeePlugin extends PeepoCloudPlugin implements PeepoCloudBungeeAPI {
    private BungeeLauncher plugin;

    private BungeeCordProxyInfo currentProxyInfo;
    private Map<String, MinecraftServerInfo> cachedServers = new HashMap<>();

    PeepoBungeePlugin(BungeeLauncher plugin) {
        super(Paths.get("nodeInfo.json"));
        this.plugin = plugin;

        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, new BungeePlayerListener(this));
        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, new BungeePluginChannelMessageListener(this));
        this.plugin.getProxy().getConfig().getServers().clear();
    }

    @Override
    public void bootstrap() {
        super.registerNetworkHandler(new BungeeNetworkHandler(this));

        super.getPacketManager().registerPacket(new PacketInUpdateProxyInfo());

        super.bootstrap();
    }

    @Override
    public void handleSuccessfulLogin() {
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

    public ServerInfo getPlayerFallback(ProxiedPlayer player) {
        List<MinecraftServerInfo> availableServers = new ArrayList<>();
        PeepoCloudPlugin.getInstance().getMinecraftGroups().forEach(group -> {
            if (group.isFallback()) {
                if (group.getFallbackPermission() == null || player.hasPermission(group.getFallbackPermission())) {
                    availableServers.addAll(PeepoCloudPlugin.getInstance().toBungee().getCachedServers(group.getName()));
                }
            }
        });
        if (availableServers.isEmpty()) {
            return null;
        }
        MinecraftServerInfo serverInfo = availableServers.get(ThreadLocalRandom.current().nextInt(availableServers.size()));
        if (serverInfo == null)
            return null;
        return this.plugin.getProxy().getServerInfo(serverInfo.getComponentName());
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
