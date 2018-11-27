package net.nevercloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.GroupMode;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.lib.utility.ZipUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.event.server.MinecraftServerTemplateCopyEvent;
import net.nevercloud.node.server.ServerFilesLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Getter
public class ServerProcess implements CloudProcess {

    private Process process;
    private Path directory;
    private MinecraftServerInfo serverInfo;
    private ProcessManager processManager;
    private long startup;
    private boolean shuttingDown = false;
    private boolean wasRunning = false;

    ServerProcess(MinecraftServerInfo serverInfo, ProcessManager processManager) {
        this.serverInfo = serverInfo;
        this.directory = NeverCloudNode.getInstance().getMinecraftGroup(serverInfo.getGroupName()).getGroupMode() == GroupMode.SAVE ?
                Paths.get("internal/savedServers/" + serverInfo.getGroupName() + "/" + serverInfo.getComponentName()) :
                Paths.get("internal/deletingServers/" + serverInfo.getGroupName() + "/" + serverInfo.getComponentName());
        this.processManager = processManager;

        if (!Files.exists(this.directory)) {
            try {
                Files.createDirectories(this.directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getName() {
        return this.serverInfo.getComponentName();
    }

    @Override
    public int getMemory() {
        return this.serverInfo.getMemory();
    }

    @Override
    public void startup() {
        this.processManager.handleProcessStart(this);

        this.loadTemplate();
        this.loadSpigot();
        this.doStart();
    }

    private void doStart() {
        try {
            this.process = Runtime.getRuntime().exec(
                    this.processManager.getConfig().getServerStartCmd().replace("%memory%", String.valueOf(this.getMemory())).split(" "),
                    null,
                    this.directory.toFile()
            );

            this.startup = System.currentTimeMillis();
            this.serverInfo.setStartup(this.startup);
            NeverCloudNode.getInstance().updateServerInfo(this.serverInfo);

            wasRunning = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSpigot() {
        Path path = Paths.get(this.directory.toString(), "server.jar");
        ServerFilesLoader.copySpigot(this.serverInfo, path);
    }

    private void loadTemplate() {
        Path dir = Paths.get("templates/" + this.serverInfo.getGroupName() + "/" + this.serverInfo.getTemplate().getName());
        MinecraftServerTemplateCopyEvent copyEvent = new MinecraftServerTemplateCopyEvent(this.serverInfo, null);
        NeverCloudNode.getInstance().getEventManager().callEvent(copyEvent);
        if (copyEvent.getInputStream() != null) {
            ZipUtils.unzipDirectory(copyEvent.getInputStream(), this.directory.toString());
        } else {
            SystemUtils.copyDirectory(dir, this.directory.toString());
        }
    }

    @Override
    public void shutdown() {
        if (this.shuttingDown || !this.wasRunning)
            return;
        this.shuttingDown = true;

        NeverCloudNode.getInstance().getExecutorService().execute(() -> {
            boolean save = NeverCloudNode.getInstance().getMinecraftGroup(this.serverInfo.getGroupName()).getGroupMode() == GroupMode.SAVE;
            if (isRunning()) {
                if (save) {
                    this.dispatchCommand("save-all");
                    SystemUtils.sleepUninterruptedly(1500);
                }
                this.dispatchCommand("stop");
                SystemUtils.sleepUninterruptedly(2500);
                if (this.isRunning()) {
                    this.process.destroyForcibly();
                    SystemUtils.sleepUninterruptedly(250);
                }
            }
            if (!save) {
                SystemUtils.deleteDirectory(this.directory);
            }
            this.processManager.handleProcessStop(this);
        });
    }

    @Override
    public String toString() {
        return this.getName() + "/memory=" + this.getMemory();
    }
}
