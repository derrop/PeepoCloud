package net.peepocloud.node;
/*
 * Created by Mc_Ruben on 24.11.2018
 */

import com.sun.management.OperatingSystemMXBean;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.config.Configuration;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.config.yaml.YamlConfigurable;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.lib.utility.network.NetworkAddress;
import net.peepocloud.node.network.ConnectableNode;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
public class CloudConfig {

    private final Path networkPath = Paths.get("networking.yml");
    private final Path mainPath = Paths.get("config.yml");
    private final Path processPath = Paths.get("processes.yml");
    private final Path credentialsPath = Paths.get("credentials.yml");

    //Network
    private Collection<ConnectableNode> connectableNodes;
    private String nodeName;
    private NetworkAddress host;

    private int maxMemory;
    private boolean autoUpdate;

    //Process
    private String bungeeStartCmd;
    private String serverStartCmd;

    //Credentials
    private String username;
    private String uniqueId;
    private String apiToken;

    private boolean useGlobalStats;

    NodeInfo loadNodeInfo(int usedMemory) {
        return new NodeInfo(this.nodeName, this.maxMemory, usedMemory, Runtime.getRuntime().availableProcessors(), SystemUtils.cpuUsageProcess());
    }

    void load() {
        this.loadNetwork();
        this.loadMain();
        this.loadProcesses();
        this.loadCredentials(); //must be loaded AFTER main
    }

    /**
     * Tries to update the unique id of this cloud from the central server
     * @return {@code true} if it was success or {@code false} if the server is offline or the credentials are incorrect
     */
    public boolean loadCredentials() {
        YamlConfigurable configurable = Files.exists(credentialsPath) ? YamlConfigurable.load(credentialsPath) : new YamlConfigurable();

        if (!configurable.contains("username") || !configurable.contains("apiToken")) {
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("support.credentialsSet.requestAccept"));
            if (PeepoCloudNode.getInstance().getLogger().readLine().equalsIgnoreCase("yes")) {
                System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("support.credentialsSet.requestUser"));
                this.username = PeepoCloudNode.getInstance().getLogger().readLine();
                System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("support.credentialsSet.requestApiToken"));
                this.apiToken = PeepoCloudNode.getInstance().getLogger().readLine();
                configurable.append("username", this.username)
                        .append("apiToken", this.apiToken)
                        .saveAsFile(credentialsPath);
            } else {
                System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("support.credentialsSet.denied"));
                return true;
            }
        } else {
            this.username = configurable.getString("username");
            this.apiToken = configurable.getString("apiToken");
        }

        try {
            URLConnection connection = new URL(SystemUtils.CENTRAL_SERVER_URL + "apiLogin").openConnection();
            connection.setRequestProperty("Peepo-UserName", this.username);
            connection.setRequestProperty("Peepo-ApiToken", this.apiToken);
            connection.setDoInput(true);
            connection.setConnectTimeout(1000);
            connection.connect();
            try (InputStream inputStream = connection.getInputStream();
                 Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                SimpleJsonObject jsonObject = new SimpleJsonObject(reader);
                if (!jsonObject.getBoolean("success")) {
                    System.out.println("&cInvalid credentials specified in the &e" + this.credentialsPath.getFileName().toString());
                    return false;
                } else {
                    this.uniqueId = jsonObject.getString("uniqueId");
                }
            }
        } catch (Exception e) {
            if (!SystemUtils.isServerOffline(e)) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void loadMain() {
        YamlConfigurable configurable = null;
        if (Files.exists(mainPath)) {
            configurable = YamlConfigurable.load(mainPath);
        } else {
            System.out.println("&cShould this cloud use global stats? Type \"yes\", if it should use global stats");
            String line = PeepoCloudNode.getInstance().getLogger().readLine();
            boolean gStats = line.equalsIgnoreCase("yes") || line.equalsIgnoreCase("true");
            configurable = new YamlConfigurable()
                    .append("autoUpdate", true)
                    .append("maxMemoryForServers", ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1024 / 1024 - 2048)
                    .append("disableGlobalStats", !gStats);
            configurable.saveAsFile(mainPath);
        }

        this.autoUpdate = configurable.getBoolean("autoUpdate");
        this.useGlobalStats = !configurable.getBoolean("disableGlobalStats");

        this.maxMemory = configurable.getInt("maxMemoryForServers");
        this.maxMemory = this.maxMemory >= 64 ? this.maxMemory : this.maxMemory * 1024; //>= 64 = gb; < 64 = mb

        if (this.maxMemory < 1024) {
            System.err.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("config.memory.notEnough").replace("%memory%", String.valueOf(this.maxMemory)));
        }
    }

    private void loadNetwork() {
        YamlConfigurable configurable = null;
        if (Files.exists(networkPath)) {
            configurable = YamlConfigurable.load(networkPath);
        } else {
            configurable = new YamlConfigurable()
                    .append("nodes", Arrays.asList(new YamlConfigurable().append("name", "Node-2").append("host", "host").append("port", 1234).asConfiguration()))
                    .append("host", new NetworkAddress(PeepoCloudNode.getInstance().getLocalAddress(), 2580))
                    .append("nodeName", "Node-1");
            configurable.saveAsFile(networkPath);
        }

        this.nodeName = configurable.getString("nodeName");

        this.connectableNodes = new ArrayList<>();
        for (Object object : (Collection) configurable.get("nodes")) {
            YamlConfigurable yamlConfigurable;
            if (object instanceof Configuration) {
                yamlConfigurable = new YamlConfigurable((Configuration) object);
            } else if (object instanceof Map) {
                Configuration configuration = new Configuration();
                configuration.self = (Map<String, Object>) object;
                yamlConfigurable = new YamlConfigurable(configuration);
            } else {
                continue;
            }

            this.connectableNodes.add(
                    new ConnectableNode(
                            yamlConfigurable.getString("name"),
                            new NetworkAddress(
                                    yamlConfigurable.getString("host"),
                                    yamlConfigurable.getInt("port")
                            )
                    )
            );
        }
        this.host = (NetworkAddress) configurable.get("host");
    }

    private void loadProcesses() {
        YamlConfigurable configurable = null;
        if (Files.exists(processPath)) {
            configurable = YamlConfigurable.load(processPath);
        } else {
            configurable = new YamlConfigurable()
                    .append("bungeeStartCommand", "java -Djline.terminal=jline.UnsupportedTerminal -Xmx%memory%M -jar process.jar")
                    .append("serverStartCommand", "java -Dcom.mojang.eula.agree=true -Xmx%memory%M -jar process.jar");
            configurable.saveAsFile(processPath);
        }

        this.bungeeStartCmd = configurable.getString("bungeeStartCommand");
        this.serverStartCmd = configurable.getString("serverStartCommand");
    }

    private void saveCredentials() {
        new YamlConfigurable()
                .append("username", this.username)
                .append("apiToken", this.apiToken)
                .saveAsFile(credentialsPath);
    }

    public void save() {
        this.saveNetwork();
        this.saveMain();
        this.saveProcesses();
        this.saveCredentials();
    }

    private void saveNetwork() {
        new YamlConfigurable()
                .append("nodes", this.connectableNodes)
                .append("host", this.host)
                .append("nodeName", this.nodeName)
                .saveAsFile(networkPath);
    }

    private void saveMain() {
        new YamlConfigurable()
                .append("autoUpdate", this.autoUpdate)
                .append("maxMemoryForServers", this.maxMemory)
                .saveAsFile(mainPath);
    }

    private void saveProcesses() {
        new YamlConfigurable()
                .append("bungeeStartCommand", this.bungeeStartCmd)
                .append("serverStartCommand", this.serverStartCmd)
                .saveAsFile(processPath);
    }

}
