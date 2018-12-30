package net.peepocloud.node.server.process;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import net.peepocloud.node.api.server.CloudProcess;

import java.util.Deque;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Consumer;

public interface CloudProcessImpl extends CloudProcess {

    Map<UUID, Consumer<String>> getScreenHandlers();

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
