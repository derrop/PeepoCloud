package net.nevercloud.node.screen.process;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.nevercloud.node.server.process.CloudProcess;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Getter
public class ProcessScreenManager {

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Map<String, LoadingScreen> screens = new HashMap<>();

    public UUID loadScreen(CloudProcess process, Consumer<String> consumer) {
        Preconditions.checkArgument(process.isRunning(), "process must be active to start a screen");
        UUID uniqueId = UUID.randomUUID();
        LoadingScreen screen;
        if (this.screens.containsKey(process.getName())) {
            screen = this.screens.get(process.getName());
        } else {
            screen = new LoadingScreen(process);
            this.executorService.execute(screen);
        }
        screen.getConsumers().put(uniqueId, consumer);
        return uniqueId;
    }

    public boolean disableScreen(CloudProcess process, UUID uniqueId) {
        if (!this.screens.containsKey(process.getName()))
            return false;
        LoadingScreen screen = this.screens.get(process.getName());
        if (!screen.getConsumers().containsKey(uniqueId))
            return false;
        screen.getConsumers().remove(uniqueId);
        if (screen.getConsumers().isEmpty()) {
            screen.stop();
            this.screens.remove(process.getName());
        }
        return true;
    }

    public boolean disableScreen(CloudProcess process) {
        if (!this.screens.containsKey(process.getName()))
            return false;
        this.screens.remove(process.getName());
        return true;
    }

}
