package net.peepocloud.plugin.bukkit.serverselector.signselector;

import net.peepocloud.lib.scheduler.Scheduler;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.server.minecraft.MinecraftState;
import net.peepocloud.lib.serverselector.signselector.AnimatedSignLayout;
import net.peepocloud.lib.serverselector.signselector.SignLayout;
import net.peepocloud.lib.serverselector.signselector.SignSelectorConfig;
import net.peepocloud.lib.serverselector.signselector.sign.ServerSign;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.plugin.bukkit.serverselector.SingleServerChildServerSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;
import java.util.*;

public class SignSelector extends SingleServerChildServerSelector<ServerSign> {
    private SignSelectorConfig config;
    private SignProvider signProvider;
    private Map<String, SignLayout> signLayouts = new HashMap<>();
    private AnimatedSignLayout loadingLayout;
    private AnimatedSignLayout maintenanceLayout;

    public SignSelector(SignSelectorConfig config, ServerSign[] serverSigns, SignLayout[] signLayouts, AnimatedSignLayout loadingLayout, AnimatedSignLayout maintenanceLayout) {
        this.config = config;
        this.signProvider = new SignProvider(super.children);

        for(SignLayout signLayout : signLayouts)
            this.signLayouts.put(signLayout.getLayoutName().toLowerCase(), signLayout);


        for(ServerSign serverSign : serverSigns) {
            MinecraftGroup minecraftGroup = PeepoCloudPlugin.getInstance().getMinecraftGroup(serverSign.getGroupName());
            if(minecraftGroup != null) {
                serverSign.setBasicLayout(this.signLayouts.get(minecraftGroup.getSignLayoutName().toLowerCase()));
                super.children.add(serverSign);
            } else
                System.out.println("Sign at " + serverSign.getPosition() + " does not have a valid group. Removing it ...");
        }

        this.loadingLayout = loadingLayout;
        this.maintenanceLayout = maintenanceLayout;

        for(MinecraftServerInfo startedServer : PeepoCloudPlugin.getInstance().getStartedMinecraftServers().complete())
            super.waitingServers.put(startedServer.getComponentName().toLowerCase(), startedServer);
    }

    @Override
    public void handleStart(Scheduler scheduler) {
        scheduler.repeat(() -> {
            this.loadingLayout.nextStep();
            this.maintenanceLayout.nextStep();

            Iterator<ServerSign> signIterator = super.children.iterator();
            while (signIterator.hasNext()) {
                ServerSign serverSign = signIterator.next();
                MinecraftServerInfo serverInfo = serverSign.getServerInfo();

                if(this.signProvider.getMinecraftSign(serverSign) == null) {
                    System.out.println("Sign at " + serverSign.getPosition() + " does not have a minecraft-sign. Removing it ...");
                    signIterator.remove();
                    continue;
                }

                if(serverInfo != null && serverInfo.getState() == MinecraftState.IN_GAME)
                    serverSign.setServerInfo(null);

                this.update(serverSign);
            }

            super.waitingServers.values().forEach(super::handleServerAdd);

        }, 0, this.config.getUpdateDelay(), true);
    }

    @Override
    public void update(ServerSign serverSign) {
        Sign minecraftSign = this.signProvider.getMinecraftSign(serverSign);

        if(minecraftSign == null)
            return;

        SignLayout signLayout = this.getServerSignLayout(serverSign);
        if(signLayout != null) {
            for (int i = 0; i < 3; i++) {
                String signLine = this.replacePlaceHolders(serverSign, signLayout.getLines()[i]);
                minecraftSign.setLine(i, signLine);
            }
        } else
            System.out.println("No SignLayout available for sign at " + serverSign.getPosition() +
                    ". Does the specific group of the sign have a valid layout set?");

        // to avoid concurrent modification
        Bukkit.getScheduler().runTask(PeepoCloudPlugin.getInstance().toBukkit().getPlugin(), () -> {
            this.updateSignBackBlock(serverSign);
            minecraftSign.update();
        });
    }

    public void updateSignBackBlock(ServerSign serverSign) {
        MinecraftServerInfo serverInfo = serverSign.getServerInfo();

        byte backBlockSubId;
        if(serverInfo == null || serverInfo.getGroup() == null)
            backBlockSubId = this.config.getBackBlockNoServerSubId();
        else {
            if(serverInfo.getGroup().isMaintenance())
                backBlockSubId = this.config.getBackBlockMaintenanceSubId();
            else if(serverInfo.getPlayers().size() >= serverInfo.getMaxPlayers())
                backBlockSubId = this.config.getBackBlockFullServerSubId();
            else if(serverInfo.getPlayers().size() == 0)
                backBlockSubId = this.config.getBackBlockEmptyServerSubId();
            else {
                backBlockSubId = this.config.getBackBlockNormalServerSubId();
            }
        }

        Location location = this.signProvider.toBukkitLocation(serverSign.getPosition());
        MaterialData materialData = location.getBlock().getState().getData();
        if(materialData instanceof org.bukkit.material.Sign) {
            Block backBlock = location.getBlock().getRelative(((org.bukkit.material.Sign) materialData).getAttachedFace());
            if(backBlock != null)
                backBlock.setTypeIdAndData(Material.getMaterial(this.config.getBackBlockMaterialName()).getId(), backBlockSubId, true);
        }
    }

    public String replacePlaceHolders(ServerSign serverSign, String text) {
        MinecraftServerInfo serverInfo = serverSign.getServerInfo();
        return serverInfo != null ? text
                .replace("%onlinePlayers%", String.valueOf(serverInfo.getPlayers().size()))
                .replace("%maxPlayers%", String.valueOf(serverInfo.getMaxPlayers()))
                .replace("%motd%", serverInfo.getMotd())
                .replace("%serverName%", serverInfo.getComponentName())
                .replace("%serverId%", String.valueOf(serverInfo.getComponentId()))
                .replace("%serverState%", serverInfo.getState().getName())
                : text.replace("%groupName%", serverSign.getGroupName());
    }

    public SignLayout getServerSignLayout(ServerSign serverSign) {
        if(serverSign.getServerInfo() == null)
            return this.loadingLayout.getCurrentLayout();

        MinecraftGroup signGroup = serverSign.getServerInfo().getGroup();
        if(signGroup == null)
            return null;

        return signGroup.isMaintenance() ? this.maintenanceLayout.getCurrentLayout() : serverSign.getBasicLayout();
    }

    public SignSelectorConfig getConfig() {
        return config;
    }

    public SignProvider getSignProvider() {
        return signProvider;
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
