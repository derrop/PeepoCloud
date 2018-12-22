package net.peepocloud.node.api.event.process;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.node.api.event.Event;
import net.peepocloud.node.api.server.CloudProcess;

@Getter
@AllArgsConstructor
/**
 * Events called for process processes running on this node instance
 */
public class ProcessEvent extends Event {
    private CloudProcess cloudProcess;
}
