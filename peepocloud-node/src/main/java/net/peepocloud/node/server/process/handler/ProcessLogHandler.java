package net.peepocloud.node.server.process.handler;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import lombok.RequiredArgsConstructor;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.server.process.CloudProcess;
import net.peepocloud.node.server.process.ProcessManager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class ProcessLogHandler implements Runnable {
    private final ProcessManager processManager;

    private StringBuffer stringBuffer = new StringBuffer();

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            for (CloudProcess value : this.processManager.getProcesses().values()) {
                this.readLog(value);
            }
            SystemUtils.sleepUninterruptedly(50);
        }
    }

    private void readLog(CloudProcess process) {
        try {
            if (!process.isRunning())
                return;

            InputStream inputStream = process.getProcess().getInputStream();

            byte[] buf = new byte[1024];
            int len;
            while (inputStream.available() > 0 && (len = inputStream.read(buf, 0, buf.length)) != -1) {
                stringBuffer.append(new String(buf, 0, len, StandardCharsets.UTF_8));
            }

            for (String line : stringBuffer.toString().split("\n")) {
                if (line.trim().isEmpty())
                    continue;
                process.getCachedLog().add(line);
                process.getScreenHandlers().values().forEach(consumer -> consumer.accept(line));
            }
            stringBuffer.setLength(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
