package net.nevercloud.node.api.event.process.bungee;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import lombok.Getter;
import net.nevercloud.lib.config.Configurable;
import net.nevercloud.node.api.event.process.ProcessEvent;
import net.nevercloud.node.server.process.CloudProcess;

@Getter
public class BungeeCordPostConfigFillEvent extends ProcessEvent {

    private Configurable configurable;

    public BungeeCordPostConfigFillEvent(CloudProcess cloudProcess, Configurable configurable) {
        super(cloudProcess);
        this.configurable = configurable;
    }
}
