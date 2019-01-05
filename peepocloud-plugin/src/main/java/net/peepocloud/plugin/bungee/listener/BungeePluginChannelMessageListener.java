package net.peepocloud.plugin.bungee.listener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.plugin.bungee.PeepoBungeePlugin;
import net.peepocloud.plugin.bungee.event.BungeeCordPluginChannelMessageEvent;
import java.util.UUID;

public class BungeePluginChannelMessageListener implements Listener {
    private PeepoBungeePlugin bungeeAPI;

    public BungeePluginChannelMessageListener(PeepoBungeePlugin bungeeAPI) {
        this.bungeeAPI = bungeeAPI;
    }

    @EventHandler
    public void handlePluginChannelMessage(BungeeCordPluginChannelMessageEvent event) {
        SimpleJsonObject data = event.getData();
        if(event.getIdentifier().equalsIgnoreCase("peepoCloud")) {
            if(data.contains("uniqueId")) {
                UUID uniqueId = data.getObject("uniqueId", UUID.class);
                ProxiedPlayer player = this.bungeeAPI.getPlugin().getProxy().getPlayer(uniqueId);
                if(player != null) {
                    switch (event.getMessage()) {
                        case "playerChat": {
                            String message = data.getString("message");
                            player.chat(message);
                            break;
                        }
                        case "playerTab": {
                            BaseComponent[] header, footer;
                            if (data.contains("header")) {
                                header = TextComponent.fromLegacyText(data.getString("header"));
                                footer = TextComponent.fromLegacyText(data.getString("footer"));
                            } else {
                                header = ComponentSerializer.parse(data.get("headerComponents"));
                                footer = ComponentSerializer.parse(data.get("footerComponents"));
                            }
                            player.setTabHeader(header, footer);
                            break;
                        }
                        case "playerMessage": {
                            BaseComponent[] message;
                            if (data.contains("message"))
                                message = TextComponent.fromLegacyText(data.getString("message"));
                            else
                                message = ComponentSerializer.parse(data.get("messageComponents"));
                            player.sendMessage(message);
                            break;
                        }
                        case "sendPlayer": {
                            String server = data.getString("server");
                            ServerInfo serverInfo = this.bungeeAPI.getPlugin().getProxy().getServerInfo(server);
                            if(serverInfo != null)
                                player.connect(serverInfo);
                            break;
                        }
                        case "playerFallback": {
                            ServerInfo fallBack = this.bungeeAPI.getPlayerFallback(player);
                            if(fallBack != null)
                                player.connect(fallBack);
                            break;
                        }
                        //String title, String subTitle, int fadeIn, int stay, int fadeOut
                        case "playerTitle": {
                            BaseComponent[] title, subTitle;
                            int fadeIn, stay, fadeOut;

                            if(data.contains("title")) {
                                title = TextComponent.fromLegacyText(data.getString("title"));
                                subTitle = TextComponent.fromLegacyText(data.getString("subTitle"));
                            } else {
                                title = ComponentSerializer.parse(data.get("titleComponents"));
                                subTitle = ComponentSerializer.parse(data.get("subTitleComponents"));
                            }
                            fadeIn = data.getInt("fadeIn");
                            stay = data.getInt("stay");
                            fadeOut = data.getInt("fadeOut");

                            player.sendTitle(this.bungeeAPI.getPlugin().getProxy().createTitle()
                                    .title(title).subTitle(subTitle).fadeIn(fadeIn).stay(stay).fadeOut(fadeOut));
                            break;
                        }
                        case "kickPlayer": {
                            BaseComponent[] reason;
                            if (data.contains("reason"))
                                reason = TextComponent.fromLegacyText(data.getString("reason"));
                            else
                                reason = ComponentSerializer.parse(data.get("reasonComponents"));
                            player.disconnect(reason);
                            break;
                        }
                        case "actionBar": {
                            BaseComponent[] message;
                            if (data.contains("message"))
                                message = TextComponent.fromLegacyText(data.getString("message"));
                            else
                                message = ComponentSerializer.parse(data.get("messageComponents"));
                            player.sendMessage(ChatMessageType.ACTION_BAR, message);
                            break;
                        }
                        default:
                            break;
                    }
                }

            }
        }
    }

}
