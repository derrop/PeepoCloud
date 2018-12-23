package net.peepocloud.plugin.api.bungee;


import net.md_5.bungee.api.plugin.Plugin;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public interface PeepoCloudBungeeAPI {

    void registerServerInfo(MinecraftServerInfo serverInfo);
    void unregisterServerInfo(MinecraftServerInfo serverInfo);

    Plugin getPlugin();

}
