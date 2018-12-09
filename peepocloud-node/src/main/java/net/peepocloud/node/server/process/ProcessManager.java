package net.peepocloud.node.server.process;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import lombok.*;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.CloudConfig;
import net.peepocloud.node.PeepoCloudNode;
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

        Arrays.asList(new ProcessStartupHandler(this), new ProcessStopHandler(this), new ProcessLogHandler(this))
                .forEach(runnable -> new Thread(runnable, runnable.getClass().getSimpleName()).start());
    }

    private CloudConfig config;
    private ServerQueue serverQueue;

    private Consumer<Integer> bungeeMemoryAdd;
    private Consumer<Integer> serverMemoryAdd;

    private Map<String, CloudProcess> processes = new HashMap<>();

    public void shutdown() {
        System.out.println("&eStopping server and proxy processes...");

        for (CloudProcess value : this.processes.values()) {
            value.shutdown();
        }

        while (isAnyProcessRunning()) {
            SystemUtils.sleepUninterruptedly(50);
        }
        SystemUtils.sleepUninterruptedly(200);

        SystemUtils.deleteDirectory(Paths.get("internal/deletingServers"));

    }

    public boolean isAnyProcessRunning() {
        for (CloudProcess value : this.processes.values()) {
            if (value.isRunning())
                return true;
        }
        return false;
    }

    public Collection<CloudProcess> getProcessesOfMinecraftGroup(String group) {
        return this.processes.values().stream().filter(process -> process instanceof ServerProcess && ((ServerProcess) process).getServerInfo().getGroupName().equalsIgnoreCase(group)).collect(Collectors.toList());
    }

    public Collection<CloudProcess> getProcessesOfBungeeGroup(String group) {
        return this.processes.values().stream().filter(process -> process instanceof BungeeProcess && ((BungeeProcess) process).getProxyInfo().getGroupName().equalsIgnoreCase(group)).collect(Collectors.toList());
    }

    public Collection<CloudProcess> getProcessesOfMinecraftGroupQueued(String group) {
        return this.serverQueue.getServerProcesses().stream().filter(process -> process instanceof ServerProcess && ((ServerProcess) process).getServerInfo().getGroupName().equalsIgnoreCase(group)).collect(Collectors.toList());
    }

    public Collection<CloudProcess> getProcessesOfBungeeGroupQueued(String group) {
        return this.serverQueue.getServerProcesses().stream().filter(process -> process instanceof BungeeProcess && ((BungeeProcess) process).getProxyInfo().getGroupName().equalsIgnoreCase(group)).collect(Collectors.toList());
}

    void handleProcessStop(CloudProcess process) {
        this.processes.remove(process.getName());
        if (process instanceof BungeeProcess) {
            this.bungeeMemoryAdd.accept(-process.getMemory());
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("process.bungee.stopped").replace("%name%", process.toString()));
        } else if (process instanceof ServerProcess) {
            this.serverMemoryAdd.accept(-process.getMemory());
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("process.server.stopped").replace("%name%", process.toString()));
        }
        PeepoCloudNode.getInstance().getScreenManager().getProcessScreenManager().disableScreen(process);
    }

    void handleProcessStart(CloudProcess process) {
        this.processes.put(process.getName(), process);
        if (process instanceof BungeeProcess) {
            this.bungeeMemoryAdd.accept(process.getMemory());
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("process.bungee.started").replace("%name%", process.toString()));
        } else if (process instanceof ServerProcess) {
            this.serverMemoryAdd.accept(process.getMemory());
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("process.server.started").replace("%name%", process.toString()));
        }
    }

}
