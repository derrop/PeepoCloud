package net.peepocloud.plugin.bungee.listener;
/*
 * Created by Mc_Ruben on 03.01.2019
 */

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.SkinConfiguration;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.player.*;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.bungee.PeepoBungeePlugin;
import net.peepocloud.plugin.network.packet.out.PacketOutPlayerLoginTry;

@AllArgsConstructor
public class BungeePlayerListener implements Listener {

    private PeepoBungeePlugin bungeeAPI;

    @EventHandler
    public void handleLogin(LoginEvent event) {
        event.registerIntent(this.bungeeAPI.getPlugin());

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
                event.completeIntent(this.bungeeAPI.getPlugin());
                return;
            }

            PlayerLoginResponse response = (PlayerLoginResponse) ((SerializationPacket) packet).getSerializable();

            if (!response.isAllowed()) {
                event.setCancelled(true);
                event.setCancelReason(TextComponent.fromLegacyText(response.getKickReason()));
            } else {
                PeepoCloudPlugin.getInstance().getCachedPlayers().put(player.getUniqueId(), player);
            }
            event.completeIntent(this.bungeeAPI.getPlugin());
        });
    }

    @EventHandler
    public void handleDisconnect(PlayerDisconnectEvent event) {
        //TODO send disconnect packet to the cloud
        PeepoCloudPlugin.getInstance().getCachedPlayers().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void handleSettingsChanged(SettingsChangedEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();
        PeepoPlayer player = PeepoCloudPlugin.getInstance().toBungee().getCachedPlayer(proxiedPlayer.getUniqueId());
        SkinConfiguration skin = proxiedPlayer.getSkinParts();
        PeepoClientSettings settings = new PeepoClientSettings(
                proxiedPlayer.getLocale().getLanguage(),
                proxiedPlayer.getViewDistance(),
                proxiedPlayer.getChatMode().ordinal(),
                proxiedPlayer.hasChatColors(),
                new PeepoPlayerSkinConfiguration(skin.hasCape(), skin.hasJacket(), skin.hasLeftSleeve(), skin.hasRightSleeve(), skin.hasLeftPants(), skin.hasRightPants(), skin.hasHat()),
                proxiedPlayer.getMainHand().ordinal()
        );
        player.setClientSettings(settings);
        player.update();
    }

    @EventHandler
    public void handleServerConnect(ServerConnectEvent event) {
        switch (event.getReason()) {
            case LOBBY_FALLBACK:
            case KICK_REDIRECT:
            case SERVER_DOWN_REDIRECT:
            case JOIN_PROXY:
            {
                ServerInfo serverInfo = bungeeAPI.getPlayerFallback(event.getPlayer());
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


}
