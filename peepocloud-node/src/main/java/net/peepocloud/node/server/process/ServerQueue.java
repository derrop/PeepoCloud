package net.peepocloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.Getter;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.process.bungee.BungeeQueuedEvent;
import net.peepocloud.node.api.event.process.server.ServerQueuedEvent;
import net.peepocloud.node.api.server.CloudProcess;
import net.peepocloud.node.network.packet.out.server.PacketOutBungeeQueued;
import net.peepocloud.node.network.packet.out.server.PacketOutServerQueued;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerQueue implements Runnable {

    private ProcessManager processManager;
    @Getter
    private BlockingDeque<CloudProcess> serverProcesses = new LinkedBlockingDeque<>();

    public static ServerQueue start(ProcessManager processManager) {
        ServerQueue serverQueue = new ServerQueue();
        serverQueue.processManager = processManager;
        Thread thread = new Thread(serverQueue, "PeepoCloud ServerQueue");
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
        if (priorityHigh) {
            this.serverProcesses.offerFirst(process);
        } else {
            this.serverProcesses.offerLast(process);
        }

        System.out.println("&aServer process queued [" + process + "]");

        if (process.isProxy()) {
            PeepoCloudNode.getInstance().sendPacketToNodes(new PacketOutBungeeQueued(process.getProxyInfo()));
            PeepoCloudNode.getInstance().getEventManager().callEvent(new BungeeQueuedEvent(process, process.getProxyInfo()));
        } else if (process.isServer()){
            PeepoCloudNode.getInstance().sendPacketToNodes(new PacketOutServerQueued(process.getServerInfo()));
            PeepoCloudNode.getInstance().getEventManager().callEvent(new ServerQueuedEvent(process, process.getServerInfo()));
        }
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
                if (process.getMemory() > PeepoCloudNode.getInstance().getCloudConfig().getMaxMemory() - PeepoCloudNode.getInstance().getMemoryUsedOnThisInstance()) {
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
