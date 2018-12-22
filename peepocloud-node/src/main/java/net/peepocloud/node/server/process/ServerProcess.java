package net.peepocloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.server.Template;
import net.peepocloud.node.api.event.network.minecraftserver.ServerStartEvent;
import net.peepocloud.lib.config.UnmodifiableConfigurable;
import net.peepocloud.lib.config.props.PropertiesConfigurable;
import net.peepocloud.lib.server.GroupMode;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.lib.utility.ZipUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.process.server.MinecraftServerConfigFillEvent;
import net.peepocloud.node.api.event.process.server.MinecraftServerPostConfigFillEvent;
import net.peepocloud.node.api.event.process.server.MinecraftServerPostTemplateCopyEvent;
import net.peepocloud.node.api.event.process.server.MinecraftServerTemplateCopyEvent;
import net.peepocloud.node.api.server.CloudProcess;
import net.peepocloud.node.api.server.TemplateStorage;
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
public class ServerProcess implements CloudProcessImpl {

    private Process process;
    private Path directory;
    @Setter
    private MinecraftServerInfo serverInfo;
    private ProcessManager processManager;
    private long startup;
    private boolean shuttingDown = false;
    private volatile boolean wasRunning = false;
    private List<String> cachedLog = new ArrayList<>();
    private Map<UUID, Consumer<String>> screenHandlers = new ConcurrentHashMap<>();
    @Setter
    private Consumer<String> networkScreenHandler;

    ServerProcess(MinecraftServerInfo serverInfo, ProcessManager processManager) {
        this.serverInfo = serverInfo;
        this.directory = PeepoCloudNode.getInstance().getMinecraftGroup(serverInfo.getGroupName()).getGroupMode() == GroupMode.SAVE ?
                Paths.get("internal/savedServers/" + serverInfo.getGroupName() + "/" + serverInfo.getComponentId()) :
                Paths.get("internal/tempServers/" + serverInfo.getGroupName() + "/" + serverInfo.getComponentId());
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
    public Template getTemplate() {
        return this.serverInfo.getTemplate();
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
    public void copyToTemplate(Template template) {
        TemplateStorage storage = PeepoCloudNode.getInstance().getTemplateStorage(template.getStorage());
        if (storage == null)
            storage = PeepoCloudNode.getInstance().getTemplateStorage("local");
        if (storage == null)
            return;
        storage.copyToTemplate(this.serverInfo, this.directory, template);
    }

    @Override
    public void copyToTemplate(Template template, String... files) {
        if (files.length == 0)
            return;
        TemplateStorage storage = PeepoCloudNode.getInstance().getTemplateStorage(template.getStorage());
        if (storage == null)
            storage = PeepoCloudNode.getInstance().getTemplateStorage("local");
        if (storage == null)
            return;
        storage.copyFilesToTemplate(this.serverInfo, this.directory, template, files);
    }

    @Override
    public void startup() {
        this.processManager.handleProcessStart(this);

        this.loadTemplate();
        this.loadSpigot();
        this.loadServerConfig();
        this.createNodeInfo();
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
        PropertiesConfigurable configurable;
        if (Files.exists(path)) {
            configurable = PropertiesConfigurable.load(path);
        } else {
            configurable = PropertiesConfigurable.load(PeepoCloudNode.class.getClassLoader().getResourceAsStream("files/server.properties"));
        }
        configurable.append("server-ip", this.serverInfo.getHost())
                .append("server-port", this.serverInfo.getPort());
        if (this.serverInfo.getMotd() != null) {
            configurable.append("motd", this.serverInfo.getMotd());
        } else {
            this.serverInfo.setMotd(configurable.getString("motd"));
        }
        if (this.serverInfo.getMaxPlayers() != -1) {
            configurable.append("max-players", this.serverInfo.getMaxPlayers());
        } else {
            int maxPlayers = -1;
            try {
                maxPlayers = Integer.parseInt(configurable.getString("max-players"));
            } catch (NumberFormatException e) {
            }
            this.serverInfo.setMaxPlayers(maxPlayers);
        }
        configurable.append("server-name", this.serverInfo.getComponentName());
        MinecraftServerConfigFillEvent configFillEvent = new MinecraftServerConfigFillEvent(this, path, configurable);
        PeepoCloudNode.getInstance().getEventManager().callEvent(configFillEvent);
        configFillEvent.getConfigurable().saveAsFile(configFillEvent.getConfigPath());
        PeepoCloudNode.getInstance().getEventManager().callEvent(new MinecraftServerPostConfigFillEvent(this, UnmodifiableConfigurable.create(configurable)));
    }

    private void createNodeInfo() {
        SimpleJsonObject nodeInfo = new SimpleJsonObject();

        nodeInfo.append("auth", new Auth(PeepoCloudNode.getInstance().getNetworkAuthKey(), this.serverInfo.getComponentName(),
                NetworkComponentType.MINECRAFT_SERVER, PeepoCloudNode.getInstance().getCloudConfig().getNodeName(), new SimpleJsonObject()));
        nodeInfo.append("networkAddress", PeepoCloudNode.getInstance().getCloudConfig().getHost().toInetSocketAddress());

        nodeInfo.saveAsFile(Paths.get(this.directory.toString(), "nodeInfo.json"));
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
                this.networkScreenHandler = null;
                this.cachedLog.clear();
            }
            if (!save) {
                SystemUtils.sleepUninterruptedly(500);
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
