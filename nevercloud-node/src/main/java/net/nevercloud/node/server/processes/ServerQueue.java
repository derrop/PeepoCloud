package net.nevercloud.node.server.processes;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerQueue implements Runnable {

    private BlockingDeque<ICloudProcess> serverProcesses = new LinkedBlockingDeque<>();

    public static ServerQueue start() {
        ServerQueue serverQueue = new ServerQueue();
        Thread thread = new Thread(serverQueue, "NeverCloud ServerQueue");
        thread.start();
        return serverQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            SystemUtils.sleepUninterruptedly(400);

            try {
                ICloudProcess process = this.serverProcesses.take();
                //TODO check free memory
                boolean memoryAvailable = false;
                if (!memoryAvailable) {
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
