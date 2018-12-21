package net.peepocloud.node.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.process.bungee.BungeeCordStartupFileCopyEvent;
import net.peepocloud.node.api.event.process.server.MinecraftServerStartupFileCopyEvent;
import net.peepocloud.node.server.bungeefile.SetupBungeeStartupFile;
import net.peepocloud.node.server.minecraftserverfile.SetupMinecraftServerStartupFile;
import net.peepocloud.node.server.process.CloudProcess;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerFilesLoader {

    private static boolean installingSpigot = false, installingBungee = false;

    public static void tryInstallSpigot() {
        Path source = Paths.get("files/server.jar");
        installServer(source);
    }

    private static void installServer(Path source) {
        if (!Files.exists(source)) {
            installingSpigot = true;
            if (!Files.exists(source.getParent())) {
                try {
                    Files.createDirectory(source.getParent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SetupMinecraftServerStartupFile.installServer(PeepoCloudNode.getInstance().getLogger(), source);
            installingSpigot = false;
        }
    }

    public static void tryInstallBungee() {
        Path source = Paths.get("files/bungee.jar");
        installBungee(source);
    }

    private static void installBungee(Path source) {
        if (!Files.exists(source)) {
            installingBungee = true;
            if (!Files.exists(source.getParent())) {
                try {
                    Files.createDirectory(source.getParent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SetupBungeeStartupFile.installBungee(PeepoCloudNode.getInstance().getLogger(), source);
            installingBungee = false;
        }
    }

    public static void copySpigot(CloudProcess cloudProcess, MinecraftServerInfo serverInfo, Path path) {
        while (installingSpigot) {
            SystemUtils.sleepUninterruptedly(150);
        }

        if (!Files.exists(path)) {
            Path source = Paths.get("files/server.jar");
            installServer(source);
            InputStream inputStream = null;
            try {
                inputStream = Files.newInputStream(source);
                MinecraftServerStartupFileCopyEvent copyEvent = new MinecraftServerStartupFileCopyEvent(cloudProcess, serverInfo, inputStream);
                PeepoCloudNode.getInstance().getEventManager().callEvent(copyEvent);
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
        while (installingBungee) {
            SystemUtils.sleepUninterruptedly(150);
        }

        if (!Files.exists(path)) {
            Path source = Paths.get("files/bungee.jar");
            installBungee(source);
            InputStream inputStream = null;
            try {
                inputStream = Files.newInputStream(source);
                BungeeCordStartupFileCopyEvent copyEvent = new BungeeCordStartupFileCopyEvent(cloudProcess, proxyInfo, inputStream);
                PeepoCloudNode.getInstance().getEventManager().callEvent(copyEvent);
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
