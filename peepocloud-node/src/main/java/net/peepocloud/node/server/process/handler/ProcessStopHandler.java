package net.peepocloud.node.server.process.handler;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import lombok.*;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.server.process.CloudProcess;
import net.peepocloud.node.server.process.ProcessManager;

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
            SystemUtils.sleepUninterruptedly(500);
        }
    }
}
