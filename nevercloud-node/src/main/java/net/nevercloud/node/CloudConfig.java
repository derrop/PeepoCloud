package net.nevercloud.node;
/*
 * Created by Mc_Ruben on 24.11.2018
 */

import com.sun.management.OperatingSystemMXBean;
import lombok.*;
import net.nevercloud.lib.config.yaml.YamlConfigurable;
import net.nevercloud.lib.node.NodeInfo;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.lib.utility.network.NetworkAddress;
import net.nevercloud.node.network.NetworkServer;
import sun.management.BaseOperatingSystemImpl;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

@Getter
@ToString
@EqualsAndHashCode
public class CloudConfig {

    private final Path networkPath = Paths.get("networking.yml");
    private final Path mainPath = Paths.get("config.yml");
    private final Path processPath = Paths.get("processes.yml");

    //Network
    private Collection<NetworkAddress> connectableNodes;
    private String nodeName;
    private NetworkAddress host;

    private int maxMemory;

    //Process
    private String bungeeStartCmd;
    private String serverStartCmd;

    NodeInfo loadNodeInfo(int usedMemory) {
        return new NodeInfo(this.nodeName, this.maxMemory, usedMemory, SystemUtils.cpuUsageProcess());
    }

    void load() {
        this.loadNetwork();
        this.loadMain();
        this.loadProcesses();
    }

    private void loadMain() {
        YamlConfigurable configurable = null;
        if (Files.exists(mainPath)) {
            configurable = YamlConfigurable.load(mainPath);
        } else {
            configurable = new YamlConfigurable()
                    .append("maxMemoryForServers", ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1024 / 1024 - 2048);
            configurable.saveAsFile(mainPath);
        }

        this.maxMemory = configurable.getInt("maxMemoryForServers");
        this.maxMemory = this.maxMemory >= 64 ? this.maxMemory : this.maxMemory * 1024; //>= 64 = gb; < 64 = mb

        if (this.maxMemory < 1024) {
            System.err.println(NeverCloudNode.getInstance().getLanguagesManager().getMessage("config.memory.notEnough").replace("%memory%", String.valueOf(this.maxMemory)));
        }
    }

    private void loadNetwork() {
        YamlConfigurable configurable = null;
        if (Files.exists(networkPath)) {
            configurable = YamlConfigurable.load(networkPath);
        } else {
            configurable = new YamlConfigurable()
                    .append("nodes", Arrays.asList(new NetworkAddress("host", 1234)))
                    .append("host", new NetworkAddress(NeverCloudNode.getInstance().getLocalAddress(), 2580))
                    .append("nodeName", "Node-1");
            configurable.saveAsFile(networkPath);
        }

        this.nodeName = configurable.getString("nodeName");

        this.connectableNodes = (Collection<NetworkAddress>) configurable.get("nodes");
        this.host = (NetworkAddress) configurable.get("host");
    }

    private void loadProcesses() {
        YamlConfigurable configurable = null;
        if (Files.exists(processPath)) {
            configurable = YamlConfigurable.load(processPath);
        } else {
            configurable = new YamlConfigurable()
                    .append("bungeeStartCommand", "java -Xmx%memory%M -jar bungee.jar")
                    .append("serverStartCommand", "java -Dcom.mojang.eula.agree=true -Xmx%memory%M -jar server.jar");
            configurable.saveAsFile(processPath);
        }

        this.bungeeStartCmd = configurable.getString("bungeeStartCommand");
        this.serverStartCmd = configurable.getString("serverStartCommand");
    }

    public void save() {
        this.saveNetwork();
        this.saveMain();
        this.saveProcesses();
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
