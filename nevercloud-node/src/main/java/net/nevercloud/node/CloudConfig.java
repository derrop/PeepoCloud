package net.nevercloud.node;
/*
 * Created by Mc_Ruben on 24.11.2018
 */

import com.sun.management.OperatingSystemMXBean;
import lombok.*;
import net.nevercloud.lib.config.yaml.YamlConfigurable;
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

    //Network
    private Collection<NetworkAddress> connectableNodes;
    private String nodeName;
    private NetworkAddress host;

    private int maxMemory;

    void load() {
        this.loadNetwork();
        this.loadMain();
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
                    .append("nodes", Arrays.asList(new NetworkAddress(NeverCloudNode.getInstance().getLocalAddress(), 0)))
                    .append("host", new NetworkAddress(NeverCloudNode.getInstance().getLocalAddress(), 2580))
                    .append("nodeName", "Node-1");
            configurable.saveAsFile(networkPath);
        }

        this.nodeName = configurable.getString("nodeName");

        this.connectableNodes = (Collection<NetworkAddress>) configurable.get("nodes");
        this.host = (NetworkAddress) configurable.get("host");
    }

    public void save() {
        this.saveNetwork();
        this.saveMain();
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

}
