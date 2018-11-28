package net.nevercloud.node.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.event.server.BungeeCordStartupFileCopyEvent;
import net.nevercloud.node.api.event.server.MinecraftServerStartupFileCopyEvent;
import net.nevercloud.node.server.bungeefile.SetupBungeeStartupFile;
import net.nevercloud.node.server.minecraftserverfile.SetupMinecraftServerStartupFile;
import net.nevercloud.node.server.process.CloudProcess;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerFilesLoader {

    public static void tryInstallSpigot() {
        Path source = Paths.get("files/server.jar");
        installServer(source);
    }

    private static void installServer(Path source) {
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
        Path source = Paths.get("files/bungee.jar");
        installBungee(source);
    }

    private static void installBungee(Path source) {
        if (!Files.exists(source)) {
            if (!Files.exists(source.getParent())) {
                try {
                    Files.createDirectory(source.getParent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SetupBungeeStartupFile.installBungee(NeverCloudNode.getInstance().getLogger(), source);
        }
    }

    public static void copySpigot(CloudProcess cloudProcess, MinecraftServerInfo serverInfo, Path path) {
        if (!Files.exists(path)) {
            Path source = Paths.get("files/server.jar");
            installServer(source);
            InputStream inputStream = null;
            try {
                inputStream = Files.newInputStream(source);
                MinecraftServerStartupFileCopyEvent copyEvent = new MinecraftServerStartupFileCopyEvent(cloudProcess, serverInfo, inputStream);
                NeverCloudNode.getInstance().getEventManager().callEvent(copyEvent);
                if (copyEvent.getInputStream() != null && copyEvent.getInputStream() != inputStream) {
                    inputStream.close();
                    inputStream = copyEvent.getInputStream();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            copy(path, source, inputStream);
        }
    }

    public static void copyBungee(CloudProcess cloudProcess, BungeeCordProxyInfo proxyInfo, Path path) {
        if (!Files.exists(path)) {
            Path source = Paths.get("files/bungee.jar");
            installBungee(source);
            InputStream inputStream = null;
            try {
                inputStream = Files.newInputStream(source);
                BungeeCordStartupFileCopyEvent copyEvent = new BungeeCordStartupFileCopyEvent(cloudProcess, proxyInfo, inputStream);
                NeverCloudNode.getInstance().getEventManager().callEvent(copyEvent);
                if (copyEvent.getInputStream() != null && copyEvent.getInputStream() != inputStream) {
                    inputStream.close();
                    inputStream = copyEvent.getInputStream();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            copy(path, source, inputStream);
        }
    }

    private static void copy(Path path, Path source, InputStream inputStream) {
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
