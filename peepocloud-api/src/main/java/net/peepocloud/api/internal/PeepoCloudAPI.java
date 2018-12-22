package net.peepocloud.api.internal;

import net.peepocloud.api.internal.bukkit.PeepoBukkitAPI;
import net.peepocloud.api.internal.bungee.PeepoBungeeAPI;
import net.peepocloud.lib.network.packet.out.server.PacketOutStopBungee;
import net.peepocloud.lib.network.packet.out.server.PacketOutStopServer;
import net.peepocloud.lib.network.packet.out.server.PacketOutUpdateBungee;
import net.peepocloud.lib.network.packet.out.server.PacketOutUpdateServer;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkClient;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.lib.network.packet.handler.ChannelHandlerAdapter;
import net.peepocloud.lib.utility.network.NetworkAddress;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

public abstract class PeepoCloudAPI {
    private static PeepoCloudAPI instance;

    private PacketManager packetManager = new PacketManager();
    private NetworkClient nodeConnector;

    public PeepoCloudAPI(File nodeInfoFile) {
        instance = this;

        if (nodeInfoFile == null) {
            this.shutdown();
            throw new NullPointerException("nodeInfoFile not specified");
        }
        SimpleJsonObject nodeInfo = SimpleJsonObject.load(nodeInfoFile.toPath());
        this.nodeConnector = new NetworkClient(nodeInfo.getObject("networkAddress", NetworkAddress.class)
                .toInetSocketAddress(), this.packetManager, new ChannelHandlerAdapter(), nodeInfo.getObject("auth", Auth.class));

        // deleting the file because the info has been read
        try {
            Files.delete(nodeInfoFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bootstrap() {
        this.nodeConnector.run();
        if (!this.nodeConnector.isConnected()) {
            this.shutdown();
            return;
        }
    }

    public void shutdown() {
        this.nodeConnector.shutdown();
    }

    public abstract boolean isBungee();

    public abstract boolean isBukkit();

    public PeepoBukkitAPI toBukkit() {
        if (this.isBukkit())
            return (PeepoBukkitAPI) this;
        throw new UnsupportedOperationException("This instance does not support bukkit");
    }

    public PeepoBungeeAPI toBungee() {
        if (this.isBungee())
            return (PeepoBungeeAPI) this;
        throw new UnsupportedOperationException("This instance does not support bungeecord");
    }


    public BungeeGroup getBungeeGroup(String name) {
        return null;
    }

    public BungeeCordProxyInfo startBungeeProxy(BungeeGroup group) {
        return null;
    }

    public BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, int memory) {
        return null;
    }

    public BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, String name) {
        return null;
    }

    public BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, String name, int id, int memory) {
        return null;
    }

    public BungeeCordProxyInfo startBungeeProxy(BungeeGroup group, String name, int memory) {
        return null;
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group) {
        return null;
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, int memory) {
        return null;
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name) {
        return null;
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name, int memory) {
        return null;
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, BungeeGroup group, String name, int id, int memory) {
        return null;
    }

    public void startBungeeProxy(BungeeCordProxyInfo proxyInfo) {

    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group) {
        return null;
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, int memory) {
        return null;
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name) {
        return null;
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name, int id, int memory) {
        return null;
    }

    public MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name, int memory) {
        return null;
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group) {
        return null;
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, int memory) {
        return null;
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name) {
        return null;
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int memory) {
        return null;
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int id, int memory) {
        return null;
    }

    public void startMinecraftServer(MinecraftServerInfo serverInfo) {

    }

    public void stopBungeeProxy(String name) {

    }

    public void stopBungeeProxy(BungeeCordProxyInfo proxyInfo) {
        this.nodeConnector.sendPacket(new PacketOutStopBungee(proxyInfo));
    }

    public void stopMinecraftServer(String name) {

    }

    public void stopMinecraftServer(MinecraftServerInfo serverInfo) {
        this.nodeConnector.sendPacket(new PacketOutStopServer(serverInfo));
    }

    public Collection<BungeeCordProxyInfo> getStartedBungeeProxies(String group) {
        return null;
    }

    public Collection<BungeeCordProxyInfo> getStartedBungeeProxies() {
        return null;
    }

    public Collection<BungeeCordProxyInfo> getBungeeProxies(String group) {
        return null;
    }

    public Collection<BungeeCordProxyInfo> getBungeeProxies() {
        return null;
    }

    public Collection<MinecraftServerInfo> getStartedMinecraftServers() {
        return null;
    }

    public Collection<MinecraftServerInfo> getStartedMinecraftServers(String group) {
        return null;
    }

    public Collection<MinecraftServerInfo> getMinecraftServers() {
        return null;
    }

    public Collection<MinecraftServerInfo> getMinecraftServers(String group) {
        return null;
    }

    public void updateProxyInfo(BungeeCordProxyInfo proxyInfo) {
        this.nodeConnector.sendPacket(new PacketOutUpdateBungee(proxyInfo));
    }

    public void updateServerInfo(MinecraftServerInfo serverInfo) {
        this.nodeConnector.sendPacket(new PacketOutUpdateServer(serverInfo));
    }

    public void updateBungeeGroup(BungeeGroup group) {

    }

    public void updateMinecraftGroup(MinecraftGroup group) {

    }


    public PacketManager getPacketManager() {
        return packetManager;
    }

    public MinecraftGroup getMinecraftGroup(String name) {
        return null;
    }

    public static PeepoCloudAPI getInstance() {
        return instance;
    }
}
