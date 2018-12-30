package net.peepocloud.node.server.process;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.server.CloudProcess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Consumer;

public interface CloudProcessImpl extends CloudProcess {

    Map<UUID, Consumer<String>> getScreenHandlers();

    String getLatestLogPath();

    @Override
    default void saveLatestLog() {
        Path path = Paths.get(getDirectory().toString(), getLatestLogPath());
        if (!Files.exists(path))
            throw new IllegalStateException("log " + path.toString() + " of server " + getName() + " does not exist");

        Path target = Paths.get("serverLogs/" + getName() + "/" + SystemUtils.DEFAULT_DATE_FORMAT.format(new Date()) + ".log");
        SystemUtils.createParent(target);

        try {
            Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Consumer<String> getNetworkScreenHandler();

    Deque<String> getCachedLog();

    void setNetworkScreenHandler(Consumer<String> consumer);

    default void handleScreenInput(String line) {
        this.getCachedLog().offerLast(line);
        if (this.getCachedLog().size() > 256) {
            this.getCachedLog().poll();
        }
        for (Consumer<String> value : this.getScreenHandlers().values()) {
            value.accept(line);
        }
        if (this.getNetworkScreenHandler() != null) {
            this.getNetworkScreenHandler().accept(line);
        }
    }

}
