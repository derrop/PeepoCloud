package net.peepocloud.node.api.server;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.api.installableplugins.InstallablePlugin;

import java.io.InputStream;
import java.nio.file.Path;

public abstract class TemplateStorage {

    public abstract String getName();

    public abstract boolean isWorking();

    /**
     * Copies the template of the given group to the {@code target} directory
     *
     * @param group    the group of the {@link Template}
     * @param template the template to copy
     * @param target   the {@link Path} where the files should be copied to
     */
    public abstract void copyToPath(MinecraftGroup group, Template template, Path target);

    /**
     * Copies the template of the given group to the {@code target} directory
     *
     * @param group    the group of the {@link Template}
     * @param template the template to copy
     * @param target   the {@link Path} where the files should be copied to
     */
    public abstract void copyToPath(BungeeGroup group, Template template, Path target);

    /**
     * Copies the {@code directory} where the {@link MinecraftServerInfo} is running to the {@link Template} of the group by the server
     *
     * @param serverInfo the info of the server
     * @param directory  the directory of the server
     * @param template   the {@link Template} of the group to copy the files to
     */
    public abstract void copyToTemplate(MinecraftServerInfo serverInfo, Path directory, Template template);

    /**
     * Copies the {@code files} in the {@code directory} where the {@link MinecraftServerInfo} is running to the {@link Template} of the group by the server
     *
     * @param files      the paths of the files relative to the {@code directory} to copy
     * @param serverInfo the info of the server
     * @param directory  the directory of the server
     * @param template   the {@link Template} of the group to copy the files to
     */
    public abstract void copyFilesToTemplate(MinecraftServerInfo serverInfo, Path directory, Template template, String[] files);

    /**
     * Copies the {@code directory} where the {@link BungeeCordProxyInfo} is running to the {@link Template} of the group by the proxy
     *
     * @param proxyInfo the info of the proxy
     * @param directory the directory of the proxy
     * @param template  the {@link Template} of the group to copy the files to
     */
    public abstract void copyToTemplate(BungeeCordProxyInfo proxyInfo, Path directory, Template template);

    /**
     * Copies the {@code files} in the {@code directory} where the {@link BungeeCordProxyInfo} is running to the {@link Template} of the group by the proxy
     *
     * @param files     the paths of the files relative to the {@code directory} to copy
     * @param proxyInfo the info of the proxy
     * @param directory the directory of the proxy
     * @param template  the {@link Template} of the group to copy the files to
     */
    public abstract void copyFilesToTemplate(BungeeCordProxyInfo proxyInfo, Path directory, Template template, String[] files);

    /**
     * Copies the {@link InputStream} to the {@link Template} of the given {@link MinecraftGroup}
     *
     * @param group       the group of the template
     * @param template    the template where the stream should be copied to
     * @param inputStream the stream from which the file is copied
     * @param path        the path relative to the template of the new file
     */
    public abstract void copyStreamToTemplate(MinecraftGroup group, Template template, InputStream inputStream, String path);

    /**
     * Copies the {@link InputStream} to the {@link Template} of the given {@link BungeeGroup}
     *
     * @param group       the group of the template
     * @param template    the template where the stream should be copied to
     * @param inputStream the stream from which the file is copied
     * @param path        the path relative to the template of the new file
     */
    public abstract void copyStreamToTemplate(BungeeGroup group, Template template, InputStream inputStream, String path);

    /**
     * Deletes the template of the given {@link MinecraftGroup}
     *
     * @param group    the group of the {@link Template} to delete
     * @param template the template to delete
     */
    public abstract void deleteTemplate(MinecraftGroup group, Template template);

    /**
     * Deletes the template of the given {@link BungeeGroup}
     *
     * @param group    the group of the {@link Template} to delete
     * @param template the template to delete
     */
    public abstract void deleteTemplate(BungeeGroup group, Template template);

    /**
     * Creates the files for the template of the given {@link MinecraftGroup}
     *
     * @param group    the group of the {@link Template} to create
     * @param template the template to create
     */
    public abstract void createTemplate(MinecraftGroup group, Template template);

    /**
     * Creates the files for the template of the given {@link BungeeGroup}
     *
     * @param group    the group of the {@link Template} to create
     * @param template the template to create
     */
    public abstract void createTemplate(BungeeGroup group, Template template);

    /**
     * Copies the global directory to the given {@link Path}
     *
     * @param target the {@link Path} where the files should be copied to
     */
    public abstract void copyGlobal(Path target);

    /**
     * Copies the given {@link InstallablePlugin} to the given {@link Path}
     *
     * @param plugin the plugin to copy
     * @param target the path to copy the jar of the plugin to
     */
    public abstract void copyInstallablePlugin(InstallablePlugin plugin, Path target);

}
