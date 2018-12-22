package net.peepocloud.node.api.server;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

import java.nio.file.Path;

public abstract class TemplateStorage {

    public abstract String getName();

    public abstract void copyToPath(String group, Template template, Path target);

    public abstract void copyToTemplate(MinecraftServerInfo serverInfo, Path directory, Template template);

    public abstract void copyFilesToTemplate(MinecraftServerInfo serverInfo, Path directory, Template template, String[] files);

    public abstract void copyToTemplate(BungeeCordProxyInfo proxyInfo, Path directory, Template template);

    public abstract void copyFilesToTemplate(BungeeCordProxyInfo proxyInfo, Path directory, Template template, String[] files);

    public abstract void deleteTemplate(MinecraftGroup group, Template template);

    public abstract void deleteTemplate(BungeeGroup group, Template template);

    public abstract void createTemplate(MinecraftGroup group, Template template);

    public abstract void createTemplate(BungeeGroup group, Template template);

}
