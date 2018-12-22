package net.peepocloud.node.api.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;

public interface CloudProcess {

    Process getProcess();

    Collection<String> getCachedLog();

    String getName();

    String getGroupName();

    Template getTemplate();

    int getMemory();

    int getPort();

    default boolean isProxy() {
        return this.getProxyInfo() != null;
    }

    default boolean isServer() {
        return this.getServerInfo() != null;
    }

    default BungeeCordProxyInfo getProxyInfo() {
        return null;
    }

    default MinecraftServerInfo getServerInfo() {
        return null;
    }

    default void copyToTemplate() {
        this.copyToTemplate(this.getTemplate());
    }

    void copyToTemplate(Template template);

    default void copyToTemplate(String... files) {
        this.copyToTemplate(this.getTemplate(), files);
    }

    void copyToTemplate(Template template, String... files);

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

    default boolean isRunning() {
        Process process = getProcess();
        return process != null && process.isAlive();
    }

    void startup();

    void shutdown();

    Path getDirectory();

}
