package net.peepocloud.plugin.api.bukkit.signselector.serverselector;


import net.peepocloud.lib.scheduler.Scheduler;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.server.minecraft.MinecraftState;
import net.peepocloud.lib.signlayout.AnimatedSignLayout;
import net.peepocloud.lib.signlayout.SignLayout;
import net.peepocloud.lib.signlayout.sign.ServerSign;
import net.peepocloud.lib.signlayout.sign.SignLocation;
import net.peepocloud.plugin.api.PeepoCloudPluginAPI;
import net.peepocloud.plugin.api.network.handler.NetworkAPIHandlerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.Listener;
import java.util.*;
import java.util.stream.Collectors;

public class SignSelector extends NetworkAPIHandlerAdapter implements Listener{
    private int updatesPerSecond;
    private List<ServerSign> serverSigns = new ArrayList<>();
    private Map<String, SignLayout> signLayouts = new HashMap<>();
    private AnimatedSignLayout loadingLayout;
    private AnimatedSignLayout maintenanceLayout;

    public SignSelector(int updatesPerSecond, ServerSign[] serverSigns, SignLayout[] signLayouts, AnimatedSignLayout loadingLayout, AnimatedSignLayout maintenanceLayout) {
        this.updatesPerSecond = updatesPerSecond;
        this.serverSigns.addAll(Arrays.asList(serverSigns));

        for(SignLayout signLayout : signLayouts)
            this.signLayouts.put(signLayout.getLayoutName(), signLayout);

        this.loadingLayout = loadingLayout;
        this.maintenanceLayout = maintenanceLayout;
    }

    public void start(Scheduler scheduler) {
        scheduler.repeat(() -> {

        }, 0, scheduler.getTicksPerSecond() / this.updatesPerSecond, true);
    }

    public void updateSign(ServerSign serverSign, MinecraftServerInfo serverInfo) {
        Sign minecraftSign = this.getMinecraftSign(serverSign);
        if(minecraftSign == null) {
            this.serverSigns.remove(serverSign);
            return;
        }

        if(serverSign.getServerInfo() != serverInfo)
            serverSign.setServerInfo(serverInfo);

        SignLayout signLayout = this.getServerSignLayout(serverSign);

        for(int i = 0; i < 3; i++) {
            String signLine = signLayout.getLines()[i]
                    .replace("%onlinePlayers%", String.valueOf(serverInfo.getPlayers().size()))
                    .replace("%maxPlayers%", String.valueOf(serverInfo.getMaxPlayers()))
                    .replace("%modt%", serverInfo.getMotd())
                    .replace("%serverName%", serverInfo.getComponentName())
                    .replace("%serverId%", String.valueOf(serverInfo.getComponentId()))
                    .replace("%groupName%", serverInfo.getGroupName())
                    .replace("%serverState%", serverInfo.getState().toString());

            minecraftSign.setLine(i, signLine);
        }

        minecraftSign.update();
    }

    public Sign getMinecraftSign(ServerSign serverSign) {
        SignLocation signLocation = serverSign.getSignLocation();
        Location bukkitLocation = new Location(Bukkit.getWorld(signLocation.getWorld()), signLocation.getX(), signLocation.getY(), signLocation.getZ());
        if(bukkitLocation.getBlock() instanceof Sign)
            return (Sign) bukkitLocation.getBlock();
        return null;
    }

    public SignLayout getServerSignLayout(ServerSign serverSign) {
        if(serverSign.getServerInfo() == null)
            return this.loadingLayout.getCurrentLayout();

        MinecraftGroup signGroup = PeepoCloudPluginAPI.getInstance().getMinecraftGroup(serverSign.getServerInfo().getGroupName());
        if(signGroup == null)
            return null;

        return signGroup.isMaintenance() ? this.maintenanceLayout.getCurrentLayout() : serverSign.getBasicLayout();
    }

    public List<ServerSign> freeSigns(String group) {
        return this.serverSigns.stream().filter(serverSign -> serverSign.getServerInfo() == null
                || serverSign.getServerInfo().getState() == MinecraftState.IN_GAME).collect(Collectors.toList());
    }

    public List<ServerSign> getServerSigns() {
        return serverSigns;
    }

    public Map<String, SignLayout> getSignLayouts() {
        return signLayouts;
    }

    public AnimatedSignLayout getLoadingLayout() {
        return loadingLayout;
    }

    public AnimatedSignLayout getMaintenanceLayout() {
        return maintenanceLayout;
    }
}
