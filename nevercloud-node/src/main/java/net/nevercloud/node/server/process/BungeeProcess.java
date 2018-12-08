package net.nevercloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.config.Configuration;
import net.nevercloud.lib.config.UnmodifiableConfigurable;
import net.nevercloud.lib.config.yaml.YamlConfigurable;
import net.nevercloud.lib.server.GroupMode;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.lib.utility.ZipUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.event.network.bungeecord.BungeeStartEvent;
import net.nevercloud.node.api.event.process.bungee.BungeeCordConfigFillEvent;
import net.nevercloud.node.api.event.process.bungee.BungeeCordPostConfigFillEvent;
import net.nevercloud.node.api.event.process.bungee.BungeeCordPostTemplateCopyEvent;
import net.nevercloud.node.api.event.process.bungee.BungeeCordTemplateCopyEvent;
import net.nevercloud.node.server.ServerFilesLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@ToString
public class BungeeProcess implements CloudProcess {
    private Path directory;
    private Process process;
    private BungeeCordProxyInfo proxyInfo;
    private ProcessManager processManager;
    private long startup;
    private boolean shuttingDown = false;
    private volatile boolean wasRunning = false;
    private List<String> cachedLog = new ArrayList<>();
    private Map<UUID, Consumer<String>> screenHandlers = new ConcurrentHashMap<>();

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
    public String getGroupName() {
        return this.proxyInfo.getGroupName();
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
    public void startup() {
        this.processManager.handleProcessStart(this);

        this.loadTemplate();
        this.loadBungee();
        this.loadServerConfig();
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

            NeverCloudNode.getInstance().getEventManager().callEvent(new BungeeStartEvent(this.proxyInfo));
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
        NeverCloudNode.getInstance().getEventManager().callEvent(copyEvent);
        if (copyEvent.getInputStream() != null) {
            ZipUtils.unzipDirectory(copyEvent.getInputStream(), this.directory.toString());
        } else {
            NeverCloudNode.getInstance().copyTemplate(this.proxyInfo.getGroupName(), this.proxyInfo.getTemplate(), this.directory);
        }
        NeverCloudNode.getInstance().getEventManager().callEvent(new BungeeCordPostTemplateCopyEvent(this));
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
                            .append("motd", "NeverCloud Lobby Server")
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
                            .append("motd", "&bNeverCloud default motd")
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
        NeverCloudNode.getInstance().getEventManager().callEvent(configFillEvent);
        configFillEvent.getConfigurable().saveAsFile(configFillEvent.getConfigPath());
        NeverCloudNode.getInstance().getEventManager().callEvent(new BungeeCordPostConfigFillEvent(this, UnmodifiableConfigurable.create(configurable)));
    }

    @Override
    public void shutdown() {
        if (this.shuttingDown || !this.wasRunning)
            return;
        this.shuttingDown = true;

        NeverCloudNode.getInstance().getExecutorService().execute(() -> {
            this.dispatchCommand("end");
            try {
                if (!this.process.waitFor(8, TimeUnit.SECONDS)) {
                    this.process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.screenHandlers.clear();
            this.cachedLog.clear();
            if (NeverCloudNode.getInstance().getBungeeGroup(this.proxyInfo.getGroupName()).getGroupMode() != GroupMode.SAVE) {
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
