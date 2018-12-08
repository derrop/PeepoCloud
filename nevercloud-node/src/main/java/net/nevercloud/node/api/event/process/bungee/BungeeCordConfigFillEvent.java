package net.nevercloud.node.api.event.process.bungee;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.nevercloud.lib.config.Configurable;
import net.nevercloud.node.api.event.process.ProcessEvent;
import net.nevercloud.node.server.process.CloudProcess;

import java.nio.file.Path;

@Getter
public class BungeeCordConfigFillEvent extends ProcessEvent {

    @Setter
    private Path configPath;
    @Setter
    private Configurable configurable;

    public BungeeCordConfigFillEvent(CloudProcess cloudProcess, Path configPath, Configurable configurable) {
        super(cloudProcess);
        this.configPath = configPath;
        this.configurable = configurable;
    }
}
