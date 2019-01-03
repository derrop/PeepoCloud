package net.peepocloud.plugin.bungee.listener;
/*
 * Created by Mc_Ruben on 03.01.2019
 */

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.lib.player.PlayerConnection;
import net.peepocloud.lib.player.PlayerLoginResponse;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.bungee.BungeeLauncher;
import net.peepocloud.plugin.bungee.network.packet.out.PacketOutPlayerLoginTry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class BungeeListener implements Listener {

    private BungeeLauncher plugin;

    @EventHandler
    public void handleLogin(LoginEvent event) {
        event.registerIntent(this.plugin);

        PendingConnection connection = event.getConnection();

        PeepoPlayer player = new PeepoPlayer(
                connection.getUniqueId(),
                connection.getName(),
                new PlayerConnection(
                        connection.getAddress().getHostName(),
                        connection.getAddress().getPort(),
                        connection.getVersion()
                ),
                PeepoCloudPlugin.getInstance().toBungee().getCurrentProxyInfo().getComponentName(),
                null,
                null
        );

        PeepoCloudPlugin.getInstance().getNodeConnector().packetQueryAsync(new PacketOutPlayerLoginTry(player)).onComplete(packet -> {
            if (!(packet instanceof SerializationPacket) || !(((SerializationPacket) packet).getSerializable() instanceof PlayerLoginResponse)) {
                event.setCancelled(true);
                event.setCancelReason(TextComponent.fromLegacyText("§cAn internal error occurred")); //TODO make configurable
                event.completeIntent(this.plugin);
                return;
            }

            PlayerLoginResponse response = (PlayerLoginResponse) ((SerializationPacket) packet).getSerializable();

            if (!response.isAllowed()) {
                event.setCancelled(true);
                event.setCancelReason(TextComponent.fromLegacyText(response.getKickReason()));
            } else {
                //TODO cache player
                event.completeIntent(this.plugin);
            }
        });
    }

    @EventHandler
    public void handleServerConnect(ServerConnectEvent event) {
        switch (event.getReason()) {
            case LOBBY_FALLBACK:
            case KICK_REDIRECT:
            case SERVER_DOWN_REDIRECT:
            case JOIN_PROXY:
            {
                ServerInfo serverInfo = fallback(event.getPlayer());
                if (serverInfo == null) {
                    event.setCancelled(true);
                    event.getPlayer().disconnect(TextComponent.fromLegacyText("§cNo fallback-server found")); //TODO configurable
                } else
                    event.setTarget(serverInfo);
            }
            break;

            default:
                break;
        }
    }

    private ServerInfo fallback(ProxiedPlayer player) {
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

}
