package net.peepocloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;
import net.peepocloud.lib.config.UnmodifiableConfigurable;
import net.peepocloud.lib.config.props.PropertiesConfigurable;
import net.peepocloud.lib.server.GroupMode;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.lib.utility.ZipUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.network.minecraftserver.ServerStartEvent;
import net.peepocloud.node.api.event.process.server.MinecraftServerConfigFillEvent;
import net.peepocloud.node.api.event.process.server.MinecraftServerPostConfigFillEvent;
import net.peepocloud.node.api.event.process.server.MinecraftServerPostTemplateCopyEvent;
import net.peepocloud.node.api.event.process.server.MinecraftServerTemplateCopyEvent;
import net.peepocloud.node.server.ServerFilesLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Getter
public class ServerProcess implements CloudProcess {

    private Process process;
    private Path directory;
    private MinecraftServerInfo serverInfo;
    private ProcessManager processManager;
    private long startup;
    private boolean shuttingDown = false;
    private volatile boolean wasRunning = false;
    private List<String> cachedLog = new ArrayList<>();
    private Map<UUID, Consumer<String>> screenHandlers = new ConcurrentHashMap<>();

    ServerProcess(MinecraftServerInfo serverInfo, ProcessManager processManager) {
        this.serverInfo = serverInfo;
        this.directory = PeepoCloudNode.getInstance().getMinecraftGroup(serverInfo.getGroupName()).getGroupMode() == GroupMode.SAVE ?
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
    public String getGroupName() {
        return this.serverInfo.getGroupName();
    }

    @Override
    public int getMemory() {
        return this.serverInfo.getMemory();
    }

    @Override
    public int getPort() {
        return this.serverInfo.getPort();
    }

    @Override
    public void startup() {
        this.processManager.handleProcessStart(this);

        this.loadTemplate();
        this.loadSpigot();
        this.loadServerConfig();
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
            PeepoCloudNode.getInstance().updateServerInfo(this.serverInfo);

            wasRunning = true;

            PeepoCloudNode.getInstance().getEventManager().callEvent(new ServerStartEvent(this.serverInfo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSpigot() {
        Path path = Paths.get(this.directory.toString(), "process.jar");
        ServerFilesLoader.copySpigot(this, this.serverInfo, path);
    }

    private void loadTemplate() {
        MinecraftServerTemplateCopyEvent copyEvent = new MinecraftServerTemplateCopyEvent(this, this.serverInfo, null);
        PeepoCloudNode.getInstance().getEventManager().callEvent(copyEvent);
        if (copyEvent.getInputStream() != null) {
            ZipUtils.unzipDirectory(copyEvent.getInputStream(), this.directory.toString());
        } else {
            PeepoCloudNode.getInstance().copyTemplate(this.serverInfo.getGroupName(), this.serverInfo.getTemplate(), this.directory);
        }
        PeepoCloudNode.getInstance().getEventManager().callEvent(new MinecraftServerPostTemplateCopyEvent(this));
    }

    private void loadServerConfig() {
        Path path = Paths.get(this.directory.toString(), "server.properties");
        PropertiesConfigurable configurable = PropertiesConfigurable.load(path) //TODO check if exists and if not load default
                .append("server-ip", this.serverInfo.getHost())
                .append("server-port", this.serverInfo.getPort());
        MinecraftServerConfigFillEvent configFillEvent = new MinecraftServerConfigFillEvent(this, path, configurable);
        PeepoCloudNode.getInstance().getEventManager().callEvent(configFillEvent);
        configFillEvent.getConfigurable().saveAsFile(configFillEvent.getConfigPath());
        PeepoCloudNode.getInstance().getEventManager().callEvent(new MinecraftServerPostConfigFillEvent(this, UnmodifiableConfigurable.create(configurable)));
    }

    @Override
    public void shutdown() {
        if (this.shuttingDown || !this.wasRunning)
            return;
        this.shuttingDown = true;

        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            boolean save = PeepoCloudNode.getInstance().getMinecraftGroup(this.serverInfo.getGroupName()).getGroupMode() == GroupMode.SAVE;
            if (isRunning()) {
                if (save) {
                    this.dispatchCommand("save-all");
                    SystemUtils.sleepUninterruptedly(750);
                }
                this.dispatchCommand("stop");
                try {
                    if (!this.process.waitFor(8, TimeUnit.SECONDS)) {
                        this.process.destroyForcibly();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.screenHandlers.clear();
                this.cachedLog.clear();
            }
            if (!save) {
                SystemUtils.deleteDirectory(this.directory);
            }
            this.processManager.handleProcessStop(this);
        });
    }

    @Override
    public String toString() {
        return this.getName() + "/memory=" + this.getMemory() + "/port=" + this.getPort();
    }
}