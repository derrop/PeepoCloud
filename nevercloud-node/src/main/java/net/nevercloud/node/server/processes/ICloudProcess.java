package net.nevercloud.node.server.processes;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public interface ICloudProcess {

    Process getProcess();

    int getMemory();

    default void dispatchCommand(String command) {
        if (isRunning()) {
            Process process = getProcess();
            OutputStream outputStream = process.getOutputStream();
            try {
                outputStream.write((command + "\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    default boolean isRunning() {
        Process process = getProcess();
        try {
            return process != null && process.isAlive() && process.getInputStream().available() != -1;
        } catch (IOException e) {
            return false;
        }
    }

    void startup();

    void shutdown();

    Path getDirectory();

}
