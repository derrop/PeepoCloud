package net.peepocloud.plugin.bukkit.serverselector.signselector;


import net.peepocloud.lib.serverselector.signselector.sign.ServerSign;
import net.peepocloud.lib.serverselector.Position;
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

    public Sign getMinecraftSign(ServerSign serverSign) {
        BlockState blockState = this.toBukkitLocation(serverSign.getPosition()).getBlock().getState();
        if(blockState instanceof Sign)
            return (Sign) blockState;
        return null;
    }

    public ServerSign getByLocation(Position position) {
        return this.serverSigns.stream().filter(serverSign -> serverSign.getPosition().equals(position)).iterator().next();
    }

    public Location toBukkitLocation(Position position) {
        return new Location(Bukkit.getWorld(position.getWorld()), position.getX(), position.getY(), position.getZ());
    }
}
