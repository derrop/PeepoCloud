package net.peepocloud.plugin.bukkit.signselector;


import net.peepocloud.lib.server.minecraft.MinecraftState;
import net.peepocloud.lib.signselector.sign.ServerSign;
import net.peepocloud.lib.signselector.sign.SignLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SignProvider {
    private List<ServerSign> serverSigns = new ArrayList<>();

    public Sign getMinecraftSign(ServerSign serverSign) {
        BlockState blockState = this.toBukkitLocation(serverSign.getSignLocation()).getBlock().getState();
        if(blockState instanceof Sign)
            return (Sign) blockState;
        return null;
    }

    public List<ServerSign> freeSigns(String group) {
        return this.serverSigns.stream().filter(serverSign -> serverSign.getGroupName().equalsIgnoreCase(group)
                && (serverSign.getServerInfo() == null || serverSign.getServerInfo().getState() != MinecraftState.LOBBY)).collect(Collectors.toList());
    }

    public ServerSign getByLocation(SignLocation signLocation) {
        return this.serverSigns.stream().filter(serverSign -> serverSign.getSignLocation().equals(signLocation)).iterator().next();
    }

    public Location toBukkitLocation(SignLocation signLocation) {
        return new Location(Bukkit.getWorld(signLocation.getWorld()), signLocation.getX(), signLocation.getY(), signLocation.getZ());
    }

    public List<ServerSign> getServerSigns() {
        return serverSigns;
    }
}
