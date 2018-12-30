package net.peepocloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.md_5.bungee.config.Configuration;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.server.Template;
import net.peepocloud.node.api.event.network.bungeecord.BungeeStartEvent;
import net.peepocloud.lib.config.UnmodifiableConfigurable;
import net.peepocloud.lib.config.yaml.YamlConfigurable;
import net.peepocloud.lib.server.GroupMode;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.lib.utility.ZipUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.process.bungee.*;
import net.peepocloud.node.api.event.process.server.MinecraftServerPluginCopyEvent;
import net.peepocloud.node.api.server.TemplateStorage;
import net.peepocloud.node.network.packet.out.api.server.PacketOutAPIProxyStarted;
import net.peepocloud.node.network.packet.out.api.server.PacketOutAPIServerStarted;
import net.peepocloud.node.server.ServerFilesLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@ToString
public class BungeeProcess implements CloudProcessImpl {
    private Path directory;
    private Process process;
    @Setter
    private BungeeCordProxyInfo proxyInfo;
    private ProcessManager processManager;
    private long startup;
    private boolean shuttingDown = false;
    private volatile boolean wasRunning = false;
    private Deque<String> cachedLog = new ConcurrentLinkedDeque<>();
    private Map<UUID, Consumer<String>> screenHandlers = new ConcurrentHashMap<>();
    @Setter
    private Consumer<String> networkScreenHandler;

    BungeeProcess(BungeeCordProxyInfo proxyInfo, ProcessManager processManager) {
        this.proxyInfo = proxyInfo;
        this.directory = PeepoCloudNode.getInstance().getBungeeGroup(proxyInfo.getGroupName()).getGroupMode() == GroupMode.SAVE ?
                Paths.get("internal/savedProxies/" + proxyInfo.getGroupName() + "/" + proxyInfo.getComponentId()) :
                Paths.get("internal/tempProxies/" + proxyInfo.getGroupName() + "/" + proxyInfo.getComponentId());
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
    public String getGroupName() {
        return this.proxyInfo.getGroupName();
    }

    @Override
    public Template getTemplate() {
        return this.proxyInfo.getTemplate();
    }

    @Override
    public int getMemory() {
        return this.proxyInfo.getMemory();
    }

    @Override
    public int getPort() {
        return this.proxyInfo.getPort();
    }

    @Override
    public void copyToTemplate(Template template) {
        TemplateStorage storage = PeepoCloudNode.getInstance().getTemplateStorage(template.getStorage());
        if (storage == null)
            storage = PeepoCloudNode.getInstance().getTemplateStorage("local");
        if (storage == null)
            return;
        storage.copyToTemplate(this.proxyInfo, this.directory, template);
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
        storage.copyFilesToTemplate(this.proxyInfo, this.directory, template, files);
    }

    @Override
    public void startup() {
        if (this.isRunning())
            return;

        this.processManager.handleProcessStart(this);

        this.loadTemplate();
        this.loadBungee();
        this.loadPlugin();
        this.loadServerConfig();
        this.createNodeInfo();
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
            PeepoCloudNode.getInstance().updateProxyInfo(this.proxyInfo);

            this.wasRunning = true;

            PeepoCloudNode.getInstance().getEventManager().callEvent(new BungeeStartEvent(this.proxyInfo));
            PeepoCloudNode.getInstance().sendPacketToServersAndProxies(new PacketOutAPIProxyStarted(this.proxyInfo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlugin() {
        InputStream inputStream = PeepoCloudNode.class.getClassLoader().getResourceAsStream("files/PeepoCloudPlugin.jar");
        Path path = Paths.get(this.directory.toString(), "plugins/PeepoCloud.jar");
        BungeeCordPluginCopyEvent event = new BungeeCordPluginCopyEvent(this, inputStream, path);
        PeepoCloudNode.getInstance().getEventManager().callEvent(event);
        if (event.getInputStream() != inputStream && event.getInputStream() != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = event.getInputStream();
        }
        if (event.getTarget() != null && event.getTarget() != path) {
            path = event.getTarget();
        }

        SystemUtils.createParent(path);

        try {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBungee() {
        Path path = Paths.get(this.directory.toString(), "process.jar");
        ServerFilesLoader.copyBungee(this, this.proxyInfo, path);
    }

    private void loadTemplate() {
        BungeeCordTemplateCopyEvent copyEvent = new BungeeCordTemplateCopyEvent(this, this.proxyInfo, null);
        PeepoCloudNode.getInstance().getEventManager().callEvent(copyEvent);
        if (copyEvent.getInputStream() != null) {
            ZipUtils.unzipDirectory(copyEvent.getInputStream(), this.directory.toString());
        } else {
            PeepoCloudNode.getInstance().copyTemplate(this.proxyInfo.getGroupName(), this.proxyInfo.getTemplate(), this.directory);
        }
        PeepoCloudNode.getInstance().getEventManager().callEvent(new BungeeCordPostTemplateCopyEvent(this));
    }

    private void loadServerConfig() {
        Path path = Paths.get(this.directory.toString(), "config.yml");
        YamlConfigurable configurable = YamlConfigurable.load(path);
        Collection<Configuration> collection = null;
        if (configurable.contains("listeners")) {
            try {
                collection = (Collection<Configuration>) ((Collection) configurable.get("listeners")).stream().map(o -> {
                    if (o instanceof Configuration)
                        return o;
                    if (o instanceof Map) {
                        Configuration configuration = new Configuration();
                        configuration.self = (Map<String, Object>) o;
                        return configuration;
                    }
                    return null;
                }).collect(Collectors.toList());
            } catch (Exception e) {
            }
        }
        if (!configurable.contains("servers")) {
            configurable.append("servers", ImmutableMap.of("Lobby-1",
                    new YamlConfigurable()
                            .append("motd", "PeepoCloud Lobby Server")
                            .append("address", "localhost:25565")
                            .append("restricted", false)
                            .asConfiguration()
                    )
            );
        }

        if (collection == null) {
            collection = new ArrayList<>();
            collection.add(
                    new YamlConfigurable()
                            .append("query_port", 25577)
                            .append("motd", "&bPeepoCloud default motd")
                            .append("priorities", new ArrayList<>(Collections.singletonList("Lobby-1")))
                            .append("bind_local_address", true)
                            .append("tab_list", "GLOBAL_PING")
                            .append("query_enabled", false)
                            .append("host", this.proxyInfo.getHost() + ":" + this.proxyInfo.getPort())
                            .append("forced_hosts", new HashMap<>())
                            .append("max_players", 1)
                            .append("tab_size", 60)
                            .append("ping_passthrough", false)
                            .append("force_default_server", false)
                            .asConfiguration()
            );
        } else {
            for (Configuration configuration : collection) {
                configuration.set("host", this.proxyInfo.getHost() + ":" + this.proxyInfo.getPort());
                break;
            }
        }

        configurable.append("listeners", collection);

        BungeeCordConfigFillEvent configFillEvent = new BungeeCordConfigFillEvent(this, path, configurable);
        PeepoCloudNode.getInstance().getEventManager().callEvent(configFillEvent);
        configFillEvent.getConfigurable().saveAsFile(configFillEvent.getConfigPath());
        PeepoCloudNode.getInstance().getEventManager().callEvent(new BungeeCordPostConfigFillEvent(this, UnmodifiableConfigurable.create(configurable)));
    }

    private void createNodeInfo() {
        SimpleJsonObject nodeInfo = new SimpleJsonObject();

        nodeInfo.append("auth", new Auth(PeepoCloudNode.getInstance().getNetworkAuthKey(), this.proxyInfo.getComponentName(),
                NetworkComponentType.BUNGEECORD, PeepoCloudNode.getInstance().getCloudConfig().getNodeName(), new SimpleJsonObject()));
        nodeInfo.append("networkAddress", PeepoCloudNode.getInstance().getCloudConfig().getHost());

        nodeInfo.saveAsFile(Paths.get(this.directory.toString(), "nodeInfo.json"));
    }

    @Override
    public void shutdown() {
        if (this.shuttingDown || !this.wasRunning)
            return;
        this.shuttingDown = true;

        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            this.dispatchCommand("end");
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
            if (PeepoCloudNode.getInstance().getBungeeGroup(this.proxyInfo.getGroupName()).getGroupMode() != GroupMode.SAVE) {
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
