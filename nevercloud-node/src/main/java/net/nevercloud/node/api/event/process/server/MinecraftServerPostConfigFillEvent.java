package net.nevercloud.node.api.event.process.server;
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
public class MinecraftServerPostConfigFillEvent extends ProcessEvent {

    private Configurable configurable;

    public MinecraftServerPostConfigFillEvent(CloudProcess cloudProcess, Configurable configurable) {
        super(cloudProcess);
        this.configurable = configurable;
    }
}
