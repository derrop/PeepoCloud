package net.peepocloud.lib;
/*
 * Created by Mc_Ruben on 26.12.2018
 */

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.chat.BaseComponent;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

import java.util.UUID;

public abstract class AbstractPeepoCloudAPI {

    private static AbstractPeepoCloudAPI instance;

    public static AbstractPeepoCloudAPI getInstance() {
        return instance;
    }

    public static void setInstance(AbstractPeepoCloudAPI instance) {
        Preconditions.checkArgument(AbstractPeepoCloudAPI.instance == null, "Instance already set");
        AbstractPeepoCloudAPI.instance = instance;
    }

    public abstract void setDebuggingOnThisComponent(boolean enable);

    public abstract void debug(String message);

    public abstract void playerChat(UUID uniqueId, String message);

    public abstract void setPlayerTabHeaderFooter(UUID uniqueId, String header, String footer);

    public abstract void setPlayerTabHeaderFooter(UUID uniqueId, BaseComponent[] header, BaseComponent[] footer);

    public abstract void sendPlayerMessage(UUID uniqueId, String message);

    public abstract void sendPlayerMessage(UUID uniqueId, BaseComponent... components);

    public abstract void sendPlayer(UUID uniqueId, String server);

    public abstract void sendPlayerFallback(UUID uniqueId);

    public abstract void sendPlayerTitle(UUID uniqueId, String title, String subTitle, int fadeIn, int stay, int fadeOut);

    public abstract void sendPlayerTitle(UUID uniqueId, BaseComponent[] title, BaseComponent[] subTitle, int fadeIn, int stay, int fadeOut);

    public abstract void kickPlayer(UUID uniqueId, String reason);

    public abstract void kickPlayer(UUID uniqueId, BaseComponent... reason);

    public abstract void sendPlayerActionBar(UUID uniqueId, String message);

    public abstract void sendPlayerActionBar(UUID uniqueId, BaseComponent... message);

    public abstract MinecraftGroup getMinecraftGroup(String name);

    public abstract BungeeGroup getBungeeGroup(String name);

    public abstract void stopMinecraftServer(MinecraftServerInfo serverInfo);

    public abstract void stopMinecraftServer(String name);

    public abstract void stopBungeeProxy(BungeeCordProxyInfo proxyInfo);

    public abstract void stopBungeeProxy(String name);

    public abstract void updateMinecraftGroup(MinecraftGroup group);

    public abstract void updateBungeeGroup(BungeeGroup group);

    public abstract void updatePlayer(PeepoPlayer player);

    public abstract int getOnlineCount();

}
