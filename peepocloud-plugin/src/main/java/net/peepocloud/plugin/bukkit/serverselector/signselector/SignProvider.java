package net.peepocloud.plugin.bukkit.serverselector.signselector;


import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.serverselector.signselector.sign.ServerSign;
import net.peepocloud.lib.serverselector.signselector.sign.SignPosition;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.network.packet.out.PacketOutAPIServerSigns;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.util.List;

public class SignProvider {
    private List<ServerSign> serverSigns;

    SignProvider(List<ServerSign> serverSigns) {
        this.serverSigns = serverSigns;
    }

    public void createSign(SignPosition position, MinecraftGroup minecraftGroup) {
        ServerSign serverSign = new ServerSign(position, minecraftGroup.getName());
        serverSign.setBasicLayout(PeepoCloudPlugin.getInstance().toBukkit().getSignSelector()
                .getSignLayouts().get(minecraftGroup.getSignLayoutName().toLowerCase()));
        this.serverSigns.add(serverSign);
        this.save();
    }

    public void removeSign(SignPosition position) {
        ServerSign serverSign = this.getByPosition(position);
        if(serverSign != null) {
            this.serverSigns.remove(serverSign);
            this.save();
        }
    }

    public void save() {
        PeepoCloudPlugin.getInstance().getNodeConnector().sendPacket(
                new PacketOutAPIServerSigns(this.serverSigns, PeepoCloudPlugin.getInstance().toBukkit().getCurrentServerInfo().getGroupName()));
    }

    public Sign getMinecraftSign(ServerSign serverSign) {
        BlockState blockState = this.toBukkitLocation(serverSign.getPosition()).getBlock().getState();
        if(blockState instanceof Sign)
            return (Sign) blockState;
        return null;
    }

    public ServerSign getByPosition(SignPosition position) {
        return this.serverSigns.stream().filter(serverSign -> serverSign.getPosition().equals(position)).findFirst().orElse(null);
    }

    public Location toBukkitLocation(SignPosition position) {
        return new Location(Bukkit.getWorld(position.getWorld()), position.getX(), position.getY(), position.getZ());
    }

    public SignPosition fromBukkitLocation(Location location, String savedOnGroup) {
        return new SignPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName(), savedOnGroup);
    }
}
