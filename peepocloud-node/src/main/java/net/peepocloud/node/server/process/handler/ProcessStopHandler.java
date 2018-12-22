package net.peepocloud.node.server.process.handler;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import lombok.*;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.server.CloudProcess;
import net.peepocloud.node.server.process.ProcessManager;

import java.util.ArrayList;

@AllArgsConstructor
public class ProcessStopHandler implements Runnable {
    private ProcessManager processManager;
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            if (this.processManager.getProcesses().isEmpty()) {
                SystemUtils.sleepUninterruptedly(2000);
            }
            if (!this.processManager.getProcesses().isEmpty()) {
                for (CloudProcess value : new ArrayList<>(this.processManager.getProcesses().values())) {
                    if (!value.isRunning()) {
                        value.shutdown();
                    }
                }
            }
            SystemUtils.sleepUninterruptedly(500);
        }
    }
}
