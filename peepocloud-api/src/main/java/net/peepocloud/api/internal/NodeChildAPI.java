package net.peepocloud.api.internal.bukkit;

import net.peepocloud.api.PeepoAPI;
import net.peepocloud.api.event.EventManager;
import net.peepocloud.api.node.NodeInfo;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.api.server.bungee.BungeeGroup;
import net.peepocloud.api.server.minecraft.MinecraftGroup;
import net.peepocloud.api.server.minecraft.MinecraftServerInfo;
import net.peepocloud.api.users.UserManager;

import java.util.Collection;

public class BukkitAPI extends PeepoAPI {


    @Override
    public boolean isSpigot() {
        // TODO: Check if spnge :smart:
        return true;
    }

    public MinecraftGroup getMinecraftGroup(String name) {
        return null;
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

    }

    public void stopMinecraftServer(String name) {

    }

    public void stopMinecraftServer(MinecraftServerInfo serverInfo) {

    }

    public void stopBungeeGroup(String name) {

    }

    public void stopMinecraftGroup(String name) {

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

    public Collection<NodeInfo> getNodeInfos() {
        return null;
    }

    public NodeInfo getBestNodeInfo(int memoryNeeded) {
        return null;
    }

    public void updateProxyInfo(BungeeCordProxyInfo proxyInfo) {

    }

    public void updateServerInfo(MinecraftServerInfo serverInfo) {

    }

    public void updateBungeeGroup(BungeeGroup group) {

    }

    public void updateMinecraftGroup(MinecraftGroup group) {

    }

    public int getMemoryUsed() {
        return 0;
    }

    public int getMaxMemory() {
        return 0;
    }

    public EventManager getEventManager() {
        return null;
    }

    public UserManager getUserManager() {
        return null;
    }
}
