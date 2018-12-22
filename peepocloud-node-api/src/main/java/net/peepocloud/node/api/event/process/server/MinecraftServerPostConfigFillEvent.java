package net.peepocloud.node.api.event.process.server;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import lombok.Getter;
import net.peepocloud.lib.config.Configurable;
import net.peepocloud.node.api.event.process.ProcessEvent;
import net.peepocloud.node.api.server.CloudProcess;

@Getter
public class MinecraftServerPostConfigFillEvent extends ProcessEvent {

    private Configurable configurable;

    public MinecraftServerPostConfigFillEvent(CloudProcess cloudProcess, Configurable configurable) {
        super(cloudProcess);
        this.configurable = configurable;
    }
}
