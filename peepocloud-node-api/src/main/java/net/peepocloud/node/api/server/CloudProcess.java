package net.peepocloud.node.api.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.peepocloud.lib.server.GroupMode;
import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.api.installableplugins.InstallablePlugin;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;

public interface CloudProcess {

    /**
     * Gets the {@link Process} of this server/proxy
     *
     * @return the {@link Process} of this instance
     */
    Process getProcess();

    /**
     * Copies the latest log of this {@link Process} to "serverLogs/[name]/dd.MM.yyyy-mm:hh"
     */
    void saveLogs();

    /**
     * Installs the given {@link InstallablePlugin} on this Process.
     * Must be called before this Process has been started
     *
     * @param plugin the plugin to install
     * @return {@code true} if the plugin was successfully put into the plugins folder (or if the {@link TemplateStorage} fails) or {@code false} if the plugin does not exist
     * @throws IllegalStateException when this method is called after this Process has been started
     */
    boolean installPlugin(InstallablePlugin plugin) throws IllegalStateException;

    /**
     * Gets all lines of the console of this process
     *
     * @return the lines of the console of this process (sorted)
     */
    Collection<String> getCachedLog();

    /**
     * Gets the name of this process
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the name of the group of this process
     *
     * @return the name of the group
     */
    String getGroupName();

    /**
     * Gets the {@link Template} from which this process has been started
     *
     * @return the template
     */
    Template getTemplate();

    /**
     * Gets the maximum memory of this process
     *
     * @return the maximum memory
     */
    int getMemory();

    /**
     * Gets the port this process is running on
     *
     * @return the port of this process
     */
    int getPort();

    /**
     * Checks if this process is a proxy instance
     *
     * @return {@code true} if it is a proxy instance or {@code false} if not
     */
    default boolean isProxy() {
        return this.getProxyInfo() != null;
    }

    /**
     * Checks if this process is a server instance
     *
     * @return {@code true} if it is a server instance or {@code false} if not
     */
    default boolean isServer() {
        return this.getServerInfo() != null;
    }

    /**
     * Gets the {@link BungeeCordProxyInfo} of this process if it is a proxy instance
     *
     * @return the proxy info of this process or {@code null} if it is no proxy instance
     */
    default BungeeCordProxyInfo getProxyInfo() {
        return null;
    }

    /**
     * Gets the {@link MinecraftServerInfo} of this process if it is a server instance
     *
     * @return the server info of this process or {@code null} if it is no server instance
     */
    default MinecraftServerInfo getServerInfo() {
        return null;
    }

    /**
     * Copies the directory of this process into the {@link Template} of this process
     */
    default void copyToTemplate() {
        this.copyToTemplate(this.getTemplate());
    }

    /**
     * Copies the directory of this process into the given {@link Template} (the template must be from the same group)
     *
     * @param template the {@link Template} to copy to
     */
    void copyToTemplate(Template template);

    /**
     * Copies the {@code files} in the directory of this process into the {@link Template} of this process
     *
     * @param files the paths of the files relative to the directory of this process
     * @param template the {@link Template} to copy to
     */
    default void copyToTemplate(String... files) {
        this.copyToTemplate(this.getTemplate(), files);
    }

    /**
     * Copies the {@code files} in the directory of this process into the given {@link Template} (the template must be from the same group)
     *
     * @param files the paths of the files relative to the directory of this process
     * @param template the {@link Template} to copy to
     */
    void copyToTemplate(Template template, String... files);

    /**
     * Dispatches the given {@code command} to this process
     *
     * @param command the command to dispatch
     */
    default void dispatchCommand(String command) {
        if (isRunning()) {
            Process process = getProcess();
            OutputStream outputStream = process.getOutputStream();
            try {
                outputStream.write((command + "\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if this process is alive
     *
     * @return {@code true} if it is running or {@code false} if not
     */
    default boolean isRunning() {
        Process process = getProcess();
        return process != null && process.isAlive();
    }

    /**
     * Starts this process if it is not running
     */
    void startup();

    /**
     * Stops this process and deletes it if the {@link GroupMode} is not {@link GroupMode#SAVE}
     */
    void shutdown();

    /**
     * Gets the directory this process is running in
     *
     * @return the directory of this process
     */
    Path getDirectory();

}
