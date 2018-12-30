package net.peepocloud.plugin.bukkit.serverselector.signselector;


import net.peepocloud.lib.serverselector.signselector.sign.ServerSign;
import net.peepocloud.lib.serverselector.Position;
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

    public void createSign(Position position, String group) {
        this.serverSigns.add(new ServerSign(position, group));
        this.save();
    }

    public void removeSign(Position position) {
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

    public ServerSign getByPosition(Position position) {
        return this.serverSigns.stream().filter(serverSign -> serverSign.getPosition().equals(position)).findFirst().orElse(null);
    }

    public Location toBukkitLocation(Position position) {
        return new Location(Bukkit.getWorld(position.getWorld()), position.getX(), position.getY(), position.getZ());
    }

    public Position fromBukkitLocation(Location location, String savedOnGroup) {
        return new Position((int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getWorld().getName(), savedOnGroup);
    }
}
