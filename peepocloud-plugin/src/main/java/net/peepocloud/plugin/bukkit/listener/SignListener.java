package net.peepocloud.plugin.bukkit.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.serverselector.signselector.sign.ServerSign;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.bukkit.serverselector.signselector.SignSelector;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {
    private SignSelector signSelector;

    public SignListener(SignSelector signSelector) {
        this.signSelector = signSelector;
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


}
