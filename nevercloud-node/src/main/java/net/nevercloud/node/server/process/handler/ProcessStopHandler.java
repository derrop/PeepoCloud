package net.nevercloud.node.server.process.handler;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import lombok.*;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.server.process.CloudProcess;
import net.nevercloud.node.server.process.ProcessManager;

@AllArgsConstructor
public class ProcessStopHandler implements Runnable {
    private ProcessManager processManager;
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            for (CloudProcess value : this.processManager.getProcesses().values()) {
                if (!value.isRunning()) {
                    value.shutdown();
                }
            }
            SystemUtils.sleepUninterruptedly(400);
        }
    }
}
