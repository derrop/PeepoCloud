package net.peepocloud.node.screen.process;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.peepocloud.node.screen.EnabledScreen;
import net.peepocloud.node.server.process.CloudProcess;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class ProcessScreenManager {

    //private ExecutorService executorService = Executors.newCachedThreadPool();
    //private Map<String, LoadingScreen> screens = new HashMap<>();

    public EnabledScreen loadScreen(CloudProcess process, Consumer<String> consumer) {
        Preconditions.checkArgument(process.isRunning(), "process must be active to start a screen");
        UUID uniqueId = UUID.randomUUID();
        new ArrayList<>(process.getCachedLog()).forEach(consumer);
        process.getScreenHandlers().put(uniqueId, consumer);
        return new EnabledScreen(process.getName(), uniqueId) {
            @Override
            public void write(String line) {
                process.dispatchCommand(line);
            }
        };
        /*LoadingScreen screen;
        if (this.screens.containsKey(process.getName())) {
            screen = this.screens.get(process.getName());
        } else {
            screen = new LoadingScreen(process);
            this.executorService.execute(screen);
        }
        screen.getConsumers().put(uniqueId, consumer);
        return uniqueId;*/
    }

    public boolean disableScreen(CloudProcess process, UUID uniqueId) {
        if (!process.getScreenHandlers().containsKey(uniqueId))
            return false;
        process.getScreenHandlers().remove(uniqueId);
        return true;
        /*if (!this.screens.containsKey(process.getName()))
            return false;
        LoadingScreen screen = this.screens.get(process.getName());
        if (!screen.getConsumers().containsKey(uniqueId))
            return false;
        screen.getConsumers().remove(uniqueId);
        if (screen.getConsumers().isEmpty()) {
            screen.stop();
            this.screens.remove(process.getName());
        }
        return true;*/
    }

    public boolean disableScreen(CloudProcess process) {
        process.getScreenHandlers().clear();
        return true;
        /*if (!this.screens.containsKey(process.getName()))
            return false;
        this.screens.remove(process.getName());
        return true;*/
    }

}