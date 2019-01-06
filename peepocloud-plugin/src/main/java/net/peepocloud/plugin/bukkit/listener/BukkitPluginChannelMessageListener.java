package net.peepocloud.plugin.bukkit.listener;

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.plugin.bukkit.event.BukkitPluginChannelMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class BukkitPluginChannelMessageListener implements Listener {

    @EventHandler
    public void handlePluginChannelMessage(BukkitPluginChannelMessageEvent event) {
        SimpleJsonObject data = event.getData();
        if(event.getIdentifier().equalsIgnoreCase("peepoCloud")) {
            if(data.contains("uniqueId")) {
                UUID uniqueId = data.getObject("uniqueId", UUID.class);
                Player player = Bukkit.getPlayer(uniqueId);
                if(player != null) {
                    switch (event.getMessage()) {
                        case "playerSound": {
                            player.playSound(player.getLocation(), data.getString("sound"), data.getLong("volume"), data.getLong("pitch"));
                            break;
                        }
                        case "playerEffect": {
                            player.playEffect(player.getLocation(), Effect.getByName(data.getString("effect")), data.getInt("data"));
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
