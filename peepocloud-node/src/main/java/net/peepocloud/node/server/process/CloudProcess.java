package net.peepocloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface CloudProcess {

    Process getProcess();

    Collection<String> getCachedLog();

    Map<UUID, Consumer<String>> getScreenHandlers();

    Consumer<String> getNetworkScreenHandler();

    void setNetworkScreenHandler(Consumer<String> consumer);

    String getName();

    String getGroupName();

    int getMemory();

    int getPort();

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
        return process != null && process.isAlive();
    }

    void startup();

    void shutdown();

    Path getDirectory();

}
