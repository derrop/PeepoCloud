package net.nevercloud.node.api.event.process.server;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.nevercloud.node.api.event.process.ProcessEvent;
import net.nevercloud.node.server.process.CloudProcess;

/**
 * Called after a template was copied into the temp directory
 */
public class MinecraftServerPostTemplateCopyEvent extends ProcessEvent {
    public MinecraftServerPostTemplateCopyEvent(CloudProcess cloudProcess) {
        super(cloudProcess);
    }
}
