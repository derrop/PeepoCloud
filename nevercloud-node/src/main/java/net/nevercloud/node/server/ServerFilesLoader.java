package net.nevercloud.node.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.nevercloud.lib.server.BungeeCordProxyInfo;
import net.nevercloud.lib.server.MinecraftServerInfo;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.events.server.BungeeCordStartupFileCopyEvent;
import net.nevercloud.node.api.events.server.MinecraftServerStartupFileCopyEvent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerFilesLoader {

    public static void tryInstallSpigot() {
        Path source = Paths.get("files/server.jar");
        if (!Files.exists(source)) {
            if (!Files.exists(source.getParent())) {
                try {
                    Files.createDirectory(source.getParent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SetupMinecraftServerStartupFile.installServer(NeverCloudNode.getInstance().getLogger(), source);
        }
    }

    public static void tryInstallBungee() {

    }

    public static void copySpigot(MinecraftServerInfo serverInfo, Path path) {
        if (!Files.exists(path)) {
            Path source = Paths.get("files/server.jar");
            if (!Files.exists(source)) {
                if (!Files.exists(source.getParent())) {
                    try {
                        Files.createDirectory(source.getParent());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                SetupMinecraftServerStartupFile.installServer(NeverCloudNode.getInstance().getLogger(), source);
            }
            InputStream inputStream = null;
            try {
                inputStream = Files.newInputStream(source);
                MinecraftServerStartupFileCopyEvent copyEvent = new MinecraftServerStartupFileCopyEvent(serverInfo, inputStream);
                NeverCloudNode.getInstance().getEventManager().callEvent(copyEvent);
                if (copyEvent.getInputStream() != null && copyEvent.getInputStream() != inputStream) {
                    inputStream.close();
                    inputStream = copyEvent.getInputStream();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (inputStream == null) {
                    inputStream = Files.newInputStream(source);
                }
                Files.copy(inputStream, path);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyBungee(BungeeCordProxyInfo proxyInfo, Path path) {
        if (!Files.exists(path)) {
            Path source = Paths.get("files/bungee.jar");
            if (!Files.exists(source)) {
                if (!Files.exists(source.getParent())) {
                    try {
                        Files.createDirectory(source.getParent());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //TODO
            }
            InputStream inputStream = null;
            try {
                inputStream = Files.newInputStream(source);
                BungeeCordStartupFileCopyEvent copyEvent = new BungeeCordStartupFileCopyEvent(proxyInfo, inputStream);
                NeverCloudNode.getInstance().getEventManager().callEvent(copyEvent);
                if (copyEvent.getInputStream() != null && copyEvent.getInputStream() != inputStream) {
                    inputStream.close();
                    inputStream = copyEvent.getInputStream();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (inputStream == null) {
                    inputStream = Files.newInputStream(source);
                }
                Files.copy(inputStream, path);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}