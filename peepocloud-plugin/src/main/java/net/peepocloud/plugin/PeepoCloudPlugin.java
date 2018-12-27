package net.peepocloud.plugin;

import net.peepocloud.lib.scheduler.Scheduler;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.plugin.api.PeepoCloudPluginAPI;
import net.peepocloud.plugin.bukkit.serverselector.signselector.SignSelector;
import net.peepocloud.plugin.bungee.PeepoBungeePlugin;
import net.peepocloud.plugin.bukkit.PeepoBukkitPlugin;
import net.peepocloud.plugin.api.network.handler.NetworkAPIHandler;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkClient;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.peepocloud.lib.utility.network.NetworkAddress;
import net.peepocloud.plugin.network.packet.in.PacketInAPIServerStarted;
import net.peepocloud.plugin.network.packet.in.PacketInAPISignSelector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public abstract class PeepoCloudPlugin extends PeepoCloudPluginAPI {
    private static PeepoCloudPlugin instance;

    private PacketManager packetManager = new PacketManager();
    private NetworkClient nodeConnector;
    private Collection<NetworkAPIHandler> networkHandlers = new ArrayList<>();
    private Scheduler scheduler = new Scheduler();

    private SignSelector signSelector;

    public PeepoCloudPlugin(Path nodeInfoFile) {
        instance = this;
        PeepoCloudPlugin.setInstance(this);

        if (nodeInfoFile == null) {
            this.shutdown();
            throw new NullPointerException("nodeInfoFile not specified");
        }
        SimpleJsonObject nodeInfo = SimpleJsonObject.load(nodeInfoFile);
        this.nodeConnector = new NetworkClient(nodeInfo.getObject("networkAddress", NetworkAddress.class)
                .toInetSocketAddress(), this.packetManager, new ChannelHandlerAdapter(), nodeInfo.getObject("auth", Auth.class));

        // deleting the file because the info has been read
        try {
            Files.delete(nodeInfoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bootstrap() {
        scheduler.getThreadPool().execute(scheduler);

        this.packetManager.registerPacket(new PacketInAPIServerStarted());
        this.packetManager.registerPacket(new PacketInAPISignSelector());

        scheduler.execute(this.nodeConnector, true);
    }

    @Override
    public void shutdown() {
        this.nodeConnector.shutdown();
    }

    @Override
    public abstract boolean isBungee();

    @Override
    public abstract boolean isBukkit();

    @Override
    public PeepoBukkitPlugin toBukkit() {
        if (this.isBukkit())
            return (PeepoBukkitPlugin) this;
        throw new UnsupportedOperationException("This instance does not support bukkit");
    }

    @Override
    public PeepoBungeePlugin toBungee() {
        if (this.isBungee())
            return (PeepoBungeePlugin) this;
        throw new UnsupportedOperationException("This instance does not support bungeecord");
    }

    public void enableSignSelector(SignSelector signSelector) {
        if(this.signSelector == null) {
            this.signSelector = signSelector;
            this.registerNetworkHandler(signSelector);
            signSelector.start(this.scheduler);
        }
    }

    @Override
    public void registerNetworkHandler(NetworkAPIHandler handler) {
        this.networkHandlers.add(handler);
    }

    @Override
    public boolean unregisterNetworkHandler(NetworkAPIHandler handler) {
        return this.networkHandlers.remove(handler);
    }

    @Override
    public MinecraftGroup getMinecraftGroup(String name) {
        return null;
    }

    @Override
    public BungeeGroup getBungeeGroup(String name) {
        return null;
    }

    @Override
    public Collection<NetworkAPIHandler> getNetworkHandlers() {
        return networkHandlers;
    }

    @Override
    public NetworkClient getNodeConnector() {
        return nodeConnector;
    }

    @Override
    public PacketManager getPacketManager() {
        return packetManager;
    }

    @Override
    public void sendPlayer(UUID uniqueId, String server) {

    }

    @Override
    public void sendPlayerActionBar(UUID uniqueId, String message) {

    }

    @Override
    public void sendPlayerMessage(UUID uniqueId, String message) {

    }

    @Override
    public void sendPlayerTitle(UUID uniqueId, String title, String subTitle, int fadeIn, int stay, int fadeOut) {

    }

    @Override
    public void kickPlayer(UUID uniqueId, String reason) {

    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    public static PeepoCloudPlugin getInstance() {
        return instance;
    }
}
