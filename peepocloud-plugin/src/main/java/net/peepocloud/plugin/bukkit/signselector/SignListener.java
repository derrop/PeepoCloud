package net.peepocloud.plugin.bukkit.signselector;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.signselector.sign.ServerSign;
import net.peepocloud.lib.signselector.sign.SignLocation;
import net.peepocloud.plugin.PeepoCloudPlugin;
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
                ServerSign serverSign = this.signSelector.getSignProvider().getByLocation(
                        new SignLocation((int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getWorld().getName()));
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
