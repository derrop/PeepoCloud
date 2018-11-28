package net.nevercloud.node.api.event.server;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.*;
import net.nevercloud.node.api.event.internal.Event;
import net.nevercloud.node.server.process.CloudProcess;

@Getter
@AllArgsConstructor
public class ProcessEvent extends Event {
    private CloudProcess cloudProcess;
}
