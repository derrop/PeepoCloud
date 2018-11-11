package net.nevercloud.node.api.events.network.node;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.node.events.Event;
import net.nevercloud.node.network.participants.NodeParticipant;

@Getter
@AllArgsConstructor
public class NodeDisconnectEvent extends Event {
    private NodeParticipant participant;
}
