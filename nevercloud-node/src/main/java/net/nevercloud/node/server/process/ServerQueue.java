package net.nevercloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.Getter;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.event.process.bungee.BungeeQueuedEvent;
import net.nevercloud.node.api.event.process.server.ServerQueuedEvent;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerQueue implements Runnable {

    private ProcessManager processManager;
    @Getter
    private BlockingDeque<CloudProcess> serverProcesses = new LinkedBlockingDeque<>();

    public static ServerQueue start(ProcessManager processManager) {
        ServerQueue serverQueue = new ServerQueue();
        serverQueue.processManager = processManager;
        Thread thread = new Thread(serverQueue, "NeverCloud ServerQueue");
        thread.start();
        return serverQueue;
    }

    public CloudProcess createProcess(BungeeCordProxyInfo proxyInfo) {
        return new BungeeProcess(
                proxyInfo,
                this.processManager
        );
    }

    public CloudProcess createProcess(MinecraftServerInfo serverInfo) {
        return new ServerProcess(
                serverInfo,
                this.processManager
        );
    }

    public void queueProcess(CloudProcess process, boolean priorityHigh) {
        if (process instanceof BungeeProcess) {
            NeverCloudNode.getInstance().getEventManager().callEvent(new BungeeQueuedEvent((BungeeProcess) process, ((BungeeProcess) process).getProxyInfo()));
        } else if (process instanceof ServerProcess){
            NeverCloudNode.getInstance().getEventManager().callEvent(new ServerQueuedEvent((ServerProcess) process, ((ServerProcess) process).getServerInfo()));
        }

        if (priorityHigh) {
            this.serverProcesses.offerFirst(process);
        } else {
            this.serverProcesses.offerLast(process);
        }

        System.out.println("&aServer process queued [" + process + "]");
    }

    public int getMemoryNeededForProcessesInQueue() {
        int i = 0;
        for (CloudProcess serverProcess : this.serverProcesses) {
            i += serverProcess.getMemory();
        }
        return i;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            SystemUtils.sleepUninterruptedly(400);

            try {
                CloudProcess process = this.serverProcesses.take();
                if (process.getMemory() > NeverCloudNode.getInstance().getCloudConfig().getMaxMemory() - NeverCloudNode.getInstance().getMemoryUsedOnThisInstance()) {
                    this.serverProcesses.offerFirst(process);
                    continue;
                }
                process.startup();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
