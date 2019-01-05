package net.peepocloud.node.server.process;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import lombok.Getter;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.CloudConfig;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.server.CloudProcess;
import net.peepocloud.node.network.packet.out.server.process.stop.PacketOutBungeeStopped;
import net.peepocloud.node.network.packet.out.server.process.stop.PacketOutServerStopped;
import net.peepocloud.node.server.process.handler.ProcessLogHandler;
import net.peepocloud.node.server.process.handler.ProcessStartupHandler;
import net.peepocloud.node.server.process.handler.ProcessStopHandler;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class ProcessManager {

    public ProcessManager(Consumer<Integer> bungeeMemoryAdd, Consumer<Integer> serverMemoryAdd, CloudConfig config) {
        this.serverQueue = ServerQueue.start(this);
        this.config = config;
        this.bungeeMemoryAdd = bungeeMemoryAdd;
        this.serverMemoryAdd = serverMemoryAdd;

        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            SystemUtils.sleepUninterruptedly(1000);
            Arrays.asList(new ProcessStartupHandler(), new ProcessStopHandler(this), new ProcessLogHandler(this))
                    .forEach(runnable -> new Thread(runnable, runnable.getClass().getSimpleName()).start());
        });
    }

    private CloudConfig config;
    private ServerQueue serverQueue;

    private Consumer<Integer> bungeeMemoryAdd;
    private Consumer<Integer> serverMemoryAdd;

    private Map<String, CloudProcessImpl> processes = new HashMap<>();

    public void shutdown() {
        System.out.println("&eStopping server and proxy processes...");

        for (CloudProcess value : this.processes.values()) {
            value.shutdown();
        }

        while (isAnyProcessRunning()) {
            SystemUtils.sleepUninterruptedly(50);
        }
        SystemUtils.sleepUninterruptedly(100);

        SystemUtils.deleteDirectory(Paths.get("internal/tempServers"));
        SystemUtils.deleteDirectory(Paths.get("internal/tempProxies"));

    }

    public boolean isAnyProcessRunning() {
        for (CloudProcess value : this.processes.values()) {
            if (value.isRunning())
                return true;
        }
        return false;
    }

    public Collection<CloudProcess> getProcessesOfMinecraftGroup(String group) {
        return this.processes.values().stream().filter(process -> process.isServer() && ((ServerProcess) process).getServerInfo().getGroupName().equalsIgnoreCase(group)).collect(Collectors.toList());
    }

    public Collection<CloudProcess> getProcessesOfBungeeGroup(String group) {
        return this.processes.values().stream().filter(process -> process.isProxy() && ((BungeeProcess) process).getProxyInfo().getGroupName().equalsIgnoreCase(group)).collect(Collectors.toList());
    }

    public Collection<CloudProcess> getProcessesOfMinecraftGroupQueued(String group) {
        return this.serverQueue.getServerProcesses().stream().filter(process -> process.isServer() && ((ServerProcess) process).getServerInfo().getGroupName().equalsIgnoreCase(group)).collect(Collectors.toList());
    }

    public Collection<CloudProcess> getProcessesOfBungeeGroupQueued(String group) {
        return this.serverQueue.getServerProcesses().stream().filter(process -> process.isProxy() && ((BungeeProcess) process).getProxyInfo().getGroupName().equalsIgnoreCase(group)).collect(Collectors.toList());
}

    void handleProcessStop(CloudProcess process) {
        this.processes.remove(process.getName());

        int exitValue = process.getProcess().exitValue();

        if (process.isProxy()) {
            this.bungeeMemoryAdd.accept(-process.getMemory());
            PeepoCloudNode.getInstance().getNetworkManager().sendPacketToNodes(new PacketOutBungeeStopped(process.getProxyInfo()));
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("process.bungee.stopped").replace("%name%", process.toString()).replace("%exitValue%", String.valueOf(exitValue)));
        } else if (process.isServer()) {
            this.serverMemoryAdd.accept(-process.getMemory());
            PeepoCloudNode.getInstance().getNetworkManager().sendPacketToNodes(new PacketOutServerStopped(process.getServerInfo()));
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("process.server.stopped").replace("%name%", process.toString()).replace("%exitValue%", String.valueOf(exitValue)));
        }
        PeepoCloudNode.getInstance().getScreenManager().handleProcessStop(process);
    }

    void handleProcessStart(CloudProcessImpl process) {
        this.processes.put(process.getName(), process);
        if (process.isProxy()) {
            this.bungeeMemoryAdd.accept(process.getMemory());
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("process.bungee.started").replace("%name%", process.toString()));
        } else if (process.isServer()) {
            this.serverMemoryAdd.accept(process.getMemory());
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("process.server.started").replace("%name%", process.toString()));
        }
    }

}
