package net.nevercloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerQueue implements Runnable {

    private BlockingDeque<CloudProcess> serverProcesses = new LinkedBlockingDeque<>();

    public static ServerQueue start() {
        ServerQueue serverQueue = new ServerQueue();
        Thread thread = new Thread(serverQueue, "NeverCloud ServerQueue");
        thread.start();
        return serverQueue;
    }

    public void queueProcess(CloudProcess process, boolean priorityHigh) {
        if (priorityHigh) {
            this.serverProcesses.offerFirst(process);
        } else {
            this.serverProcesses.offerLast(process);
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
