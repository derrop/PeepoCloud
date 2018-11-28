package net.nevercloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.lib.server.GroupMode;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.lib.utility.ZipUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.event.server.BungeeCordTemplateCopyEvent;
import net.nevercloud.node.api.event.server.MinecraftServerTemplateCopyEvent;
import net.nevercloud.node.server.ServerFilesLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Getter
@ToString
public class BungeeProcess implements CloudProcess {
    private Path directory;
    private Process process;
    private BungeeCordProxyInfo proxyInfo;
    private ProcessManager processManager;
    private long startup;
    private boolean shuttingDown = false;
    private boolean wasRunning = false;

    BungeeProcess(BungeeCordProxyInfo proxyInfo, ProcessManager processManager) {
        this.proxyInfo = proxyInfo;
        this.directory = NeverCloudNode.getInstance().getBungeeGroup(proxyInfo.getGroupName()).getGroupMode() == GroupMode.SAVE ?
                Paths.get("internal/savedProxies/" + proxyInfo.getGroupName() + "/" + proxyInfo.getComponentName()) :
                Paths.get("internal/deletingProxies/" + proxyInfo.getGroupName() + "/" + proxyInfo.getComponentName());
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
        return this.proxyInfo.getComponentName();
    }

    @Override
    public int getMemory() {
        return this.proxyInfo.getMemory();
    }

    @Override
    public void startup() {
        this.processManager.handleProcessStart(this);

        this.loadTemplate();
        this.loadBungee();
        this.doStart();
    }

    private void doStart() {
        try {
            this.process = Runtime.getRuntime().exec(
                    this.processManager.getConfig().getBungeeStartCmd().replace("%memory%", String.valueOf(this.getMemory())).split(" "),
                    null,
                    this.directory.toFile()
            );
            this.startup = System.currentTimeMillis();
            this.proxyInfo.setStartup(this.startup);
            NeverCloudNode.getInstance().updateProxyInfo(this.proxyInfo);

            this.wasRunning = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBungee() {
        Path path = Paths.get(this.directory.toString(), "bungee.jar");
        ServerFilesLoader.copyBungee(this, this.proxyInfo, path);
    }

    private void loadTemplate() {
        Path dir = Paths.get("templates/" + this.proxyInfo.getGroupName() + "/" + this.proxyInfo.getTemplate().getName());
        BungeeCordTemplateCopyEvent copyEvent = new BungeeCordTemplateCopyEvent(this, this.proxyInfo, null);
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
            this.dispatchCommand("end");
            SystemUtils.sleepUninterruptedly(1500);
            if (this.isRunning()) {
                this.process.destroyForcibly();
                SystemUtils.sleepUninterruptedly(250);
            }
            if (NeverCloudNode.getInstance().getBungeeGroup(this.proxyInfo.getGroupName()).getGroupMode() != GroupMode.SAVE) {
                SystemUtils.deleteDirectory(this.directory);
            }
            this.processManager.handleProcessStop(this);
        });
    }
}
