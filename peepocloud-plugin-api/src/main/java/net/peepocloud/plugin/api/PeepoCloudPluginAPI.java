package net.peepocloud.plugin.api;


import com.google.common.base.Preconditions;
import net.peepocloud.lib.AbstractPeepoCloudAPI;
import net.peepocloud.lib.network.NetworkClient;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.scheduler.Scheduler;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.plugin.api.bukkit.PeepoCloudBukkitAPI;
import net.peepocloud.plugin.api.bungee.PeepoCloudBungeeAPI;
import net.peepocloud.plugin.api.network.handler.NetworkAPIHandler;

import java.util.Collection;

public abstract class PeepoCloudPluginAPI extends AbstractPeepoCloudAPI {
    private static PeepoCloudPluginAPI instance;

    public static void setInstance(PeepoCloudPluginAPI instance) {
        Preconditions.checkArgument(PeepoCloudPluginAPI.instance == null, "Instance already set");
        PeepoCloudPluginAPI.instance = instance;
        AbstractPeepoCloudAPI.setInstance(instance);
    }

    public static PeepoCloudPluginAPI getInstance() {
        return instance;
    }

    public abstract void bootstrap();
    public abstract void shutdown();

    public abstract boolean isBukkit();
    public abstract boolean isBungee();
    public abstract PeepoCloudBukkitAPI toBukkit();
    public abstract PeepoCloudBungeeAPI toBungee();

    public abstract void registerNetworkHandler(NetworkAPIHandler handler);
    public abstract boolean unregisterNetworkHandler(NetworkAPIHandler handler);

    public abstract MinecraftGroup getMinecraftGroup(String name);
    public abstract BungeeGroup getBungeeGroup(String name);

    public abstract PacketManager getPacketManager();
    public abstract NetworkClient getNodeConnector();
    public abstract Collection<NetworkAPIHandler> getNetworkHandlers();
    public abstract Scheduler getScheduler();



}
