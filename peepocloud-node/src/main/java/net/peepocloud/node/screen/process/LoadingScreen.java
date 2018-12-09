package net.peepocloud.node.screen.process;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import lombok.Data;
import net.peepocloud.node.server.process.CloudProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Data
public class LoadingScreen implements Runnable {

    public LoadingScreen(CloudProcess process) {
        this.process = process;
    }

    private CloudProcess process;
    private Map<UUID, Consumer<String>> consumers = new HashMap<>();
    private Thread thread;

    @Override
    public void run() {
        this.thread = Thread.currentThread();
        try (Reader reader = new InputStreamReader(this.process.getProcess().getInputStream(), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while (!this.thread.isInterrupted() && this.process.isRunning() && (line = bufferedReader.readLine()) != null) {
                for (Consumer<String> consumer : this.consumers.values()) {
                    consumer.accept(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread.stop();
        }
    }
}
