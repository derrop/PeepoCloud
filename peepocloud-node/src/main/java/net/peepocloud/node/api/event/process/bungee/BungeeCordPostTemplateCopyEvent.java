package net.peepocloud.node.api.event.process.bungee;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.peepocloud.node.api.event.process.ProcessEvent;
import net.peepocloud.node.server.process.CloudProcess;

/**
 * Called after a template was copied into the temp directory
 */
public class BungeeCordPostTemplateCopyEvent extends ProcessEvent {
    public BungeeCordPostTemplateCopyEvent(CloudProcess cloudProcess) {
        super(cloudProcess);
    }
}
