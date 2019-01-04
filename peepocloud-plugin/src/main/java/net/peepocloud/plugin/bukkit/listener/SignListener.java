package net.peepocloud.plugin.bukkit.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.serverselector.signselector.SignLayout;
import net.peepocloud.lib.serverselector.signselector.sign.ServerSign;
import net.peepocloud.lib.serverselector.signselector.sign.SignPosition;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.bukkit.PeepoBukkitPlugin;
import net.peepocloud.plugin.bukkit.serverselector.signselector.SignSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {
    private PeepoBukkitPlugin bukkitAPI;
    private SignSelector signSelector;

    public SignListener(PeepoBukkitPlugin bukkitAPI, SignSelector signSelector) {
        this.bukkitAPI = bukkitAPI;
        this.signSelector = signSelector;
        if(signSelector.getConfig().isSignTitle())
            this.startTitleScheduler();
    }

    @EventHandler
    public void handleSignClick(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if(block.getState() instanceof Sign) {
                Location location = block.getLocation();
                ServerSign serverSign = this.signSelector.getSignProvider().getByPosition(
                        this.signSelector.getSignProvider().fromBukkitLocation(location, PeepoCloudPlugin
                                .getInstance().toBukkit().getCurrentServerInfo().getGroupName()));
                if(serverSign != null) {
                    MinecraftServerInfo serverInfo = serverSign.getServerInfo();
                    if(serverInfo != null && !PeepoCloudPlugin.getInstance().getMinecraftGroup(serverInfo.getGroupName()).isMaintenance()) {
                        ByteArrayDataOutput output = ByteStreams.newDataOutput();
                        output.writeUTF("Connect");
                        output.writeUTF(serverInfo.getComponentName());
                        event.getPlayer().sendPluginMessage(PeepoCloudPlugin.getInstance().toBukkit().getPlugin(), "BungeeCord", output.toByteArray());
                    }
                }
            }
        }
    }

    private void startTitleScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.bukkitAPI.getPlugin(), () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                Block targetBlock = player.getTargetBlock(null, 10);
                if(targetBlock != null) {
                    SignPosition position = this.signSelector.getSignProvider().fromBukkitLocation(
                            targetBlock.getLocation(), bukkitAPI.getCurrentServerInfo().getGroupName());
                    ServerSign serverSign = this.signSelector.getSignProvider().getByPosition(position);
                    if(serverSign != null) {
                        SignLayout signLayout = this.signSelector.getServerSignLayout(serverSign);
                        String firstTitle = this.signSelector.replacePlaceHolders(serverSign, signLayout.getSignTitle()[0]);
                        String secondTitle = this.signSelector.replacePlaceHolders(serverSign, signLayout.getSignTitle()[1]);
                        player.sendTitle(firstTitle, secondTitle, 10, this.signSelector.getConfig().getUpdateDelay() * 20, 20);
                    }
                }
            }
        }, 0, this.signSelector.getConfig().getUpdateDelay());
    }


}
