package net.peepocloud.plugin.bukkit.signselector;

import net.peepocloud.lib.scheduler.Scheduler;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.server.minecraft.MinecraftState;
import net.peepocloud.lib.signselector.AnimatedSignLayout;
import net.peepocloud.lib.signselector.SignLayout;
import net.peepocloud.lib.signselector.SignSelectorConfig;
import net.peepocloud.lib.signselector.sign.ServerSign;
import net.peepocloud.lib.signselector.sign.SignLocation;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.api.network.handler.NetworkAPIHandlerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;
import java.util.*;
import java.util.stream.Collectors;

public class SignSelector extends NetworkAPIHandlerAdapter {
    private SignSelectorConfig config;
    private Collection<ServerSign> serverSigns = new ArrayList<>();
    private Map<String, SignLayout> signLayouts = new HashMap<>();
    private AnimatedSignLayout loadingLayout;
    private AnimatedSignLayout maintenanceLayout;

    private Collection<MinecraftServerInfo> waitingServers = new ArrayList<>();

    public SignSelector(SignSelectorConfig config, ServerSign[] serverSigns, SignLayout[] signLayouts, AnimatedSignLayout loadingLayout, AnimatedSignLayout maintenanceLayout) {
        this.config = config;

        for(SignLayout signLayout : signLayouts)
            this.signLayouts.put(signLayout.getLayoutName().toLowerCase(), signLayout);

        for(ServerSign serverSign : serverSigns) {
            serverSign.setBasicLayout(this.signLayouts.get(PeepoCloudPlugin.getInstance()
                    .getMinecraftGroup(serverSign.getServerInfo().getGroupName()).getSignLayoutName()));
            this.serverSigns.add(serverSign);
        }

        this.loadingLayout = loadingLayout;
        this.maintenanceLayout = maintenanceLayout;
    }

    public void start(Scheduler scheduler) {
        scheduler.repeat(() -> {
            this.loadingLayout.nextStep();
            this.maintenanceLayout.nextStep();

            Iterator<ServerSign> signIterator = this.serverSigns.iterator();
            while (signIterator.hasNext()) {
                ServerSign serverSign = signIterator.next();

                if(this.getMinecraftSign(serverSign) == null) {
                    signIterator.remove();
                    continue;
                }

                MinecraftServerInfo serverInfo = serverSign.getServerInfo();
                if(serverInfo != null && serverInfo.getState() != MinecraftState.LOBBY)
                    serverSign.setServerInfo(null);

                this.updateSign(serverSign);
            }

            this.waitingServers.forEach(this::handleServerAdd);

        }, 0, this.config.getUpdateDelay(), true);
    }

    public void updateSign(ServerSign serverSign, MinecraftServerInfo serverInfo) {
        Sign minecraftSign = this.getMinecraftSign(serverSign);

        if(minecraftSign == null)
            return;

        if(serverInfo != null && serverSign.getServerInfo() != serverInfo)
            serverSign.setServerInfo(serverInfo);

        SignLayout signLayout = this.getServerSignLayout(serverSign);

        for(int i = 0; i < 3; i++) {
            String signLine = serverInfo != null ? signLayout.getLines()[i]
                    .replace("%onlinePlayers%", String.valueOf(serverInfo.getPlayers().size()))
                    .replace("%maxPlayers%", String.valueOf(serverInfo.getMaxPlayers()))
                    .replace("%motd%", serverInfo.getMotd())
                    .replace("%serverName%", serverInfo.getComponentName())
                    .replace("%serverId%", String.valueOf(serverInfo.getComponentId()))
                    .replace("%serverState%", serverInfo.getState().getName()) : signLayout.getLines()[i];

            minecraftSign.setLine(i, signLine.replace("%groupName%", serverSign.getGroupName()));
        }

        // to avoid concurrent modification
        Bukkit.getScheduler().runTask(PeepoCloudPlugin.getInstance().toBukkit().getPlugin(), () -> {
            this.updateSignBackBlock(serverSign);
            minecraftSign.update();
        });
    }

    public void updateSign(ServerSign serverSign) {
        this.updateSign(serverSign, serverSign.getServerInfo());
    }

    public void updateSignBackBlock(ServerSign serverSign) {
        MinecraftServerInfo serverInfo = serverSign.getServerInfo();

        byte backBlockSubId;
        if(serverInfo == null)
            backBlockSubId = this.config.getBackBlockNoServerSubId();
        else {
            if(PeepoCloudPlugin.getInstance().getMinecraftGroup(serverInfo.getGroupName()).isMaintenance())
                backBlockSubId = this.config.getBackBlockMaintenanceSubId();
            else if(serverInfo.getPlayers().size() >= serverInfo.getMaxPlayers())
                backBlockSubId = this.config.getBackBlockFullServerSubId();
            else if(serverInfo.getPlayers().size() == 0)
                backBlockSubId = this.config.getBackBlockEmptyServerSubId();
            else {
                backBlockSubId = this.config.getBackBlockNormalServerSubId();
            }
        }

        Location location = this.toBukkitLocation(serverSign.getSignLocation());
        MaterialData materialData = location.getBlock().getState().getData();
        if(materialData instanceof org.bukkit.material.Sign) {
            Block backBlock = location.getBlock().getRelative(((org.bukkit.material.Sign) materialData).getAttachedFace());
            if(backBlock != null)
                backBlock.setTypeIdAndData(Material.getMaterial(this.config.getBackBlockMaterialName()).getId(), backBlockSubId, true);
        }
    }

    public ServerSign getByLocation(SignLocation signLocation) {
        return this.serverSigns.stream().filter(serverSign -> serverSign.getSignLocation().equals(signLocation)).iterator().next();
    }

    public Sign getMinecraftSign(ServerSign serverSign) {
        BlockState blockState = this.toBukkitLocation(serverSign.getSignLocation()).getBlock().getState();
        if(blockState instanceof Sign)
            return (Sign) blockState;
        return null;
    }

    public SignLayout getServerSignLayout(ServerSign serverSign) {
        if(serverSign.getServerInfo() == null)
            return this.loadingLayout.getCurrentLayout();

        MinecraftGroup signGroup = PeepoCloudPlugin.getInstance().getMinecraftGroup(serverSign.getServerInfo().getGroupName());
        if(signGroup == null)
            return null;

        return signGroup.isMaintenance() ? this.maintenanceLayout.getCurrentLayout() : serverSign.getBasicLayout();
    }

    public List<ServerSign> freeSigns(String group) {
        return this.serverSigns.stream().filter(serverSign -> serverSign.getGroupName().equalsIgnoreCase(group)
                && (serverSign.getServerInfo() == null || serverSign.getServerInfo().getState() != MinecraftState.LOBBY)).collect(Collectors.toList());
    }

    public Location toBukkitLocation(SignLocation signLocation) {
        return new Location(Bukkit.getWorld(signLocation.getWorld()), signLocation.getX(), signLocation.getY(), signLocation.getZ());
    }

    @Override
    public void handleServerAdd(MinecraftServerInfo serverInfo) {
        List<ServerSign> freeSigns = this.freeSigns(serverInfo.getGroupName());
        if(freeSigns.size() > 0) {
            this.waitingServers.remove(serverInfo);
            this.updateSign(freeSigns.get(0), serverInfo);
        } else if(this.waitingServers.contains(serverInfo))
            this.waitingServers.add(serverInfo);
    }

    @Override
    public void handleServerStop(MinecraftServerInfo serverInfo) {
        for(ServerSign serverSign : this.serverSigns) {
            if(serverSign.getServerInfo() != null && serverSign.getServerInfo().getComponentName().equalsIgnoreCase(serverInfo.getComponentName()))
                serverSign.setServerInfo(null);
        }

        this.waitingServers.removeIf(current -> current.getComponentName().equalsIgnoreCase(serverInfo.getComponentName()));
    }

    public Collection<ServerSign> getServerSigns() {
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
