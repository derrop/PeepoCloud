package net.nevercloud.node.api.event.network.node;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.node.api.event.internal.Event;
import net.nevercloud.node.network.participant.NodeParticipant;

@Getter
public class NodeConnectEvent extends NetworkNodeEvent {
    public NodeConnectEvent(NodeParticipant participant) {
        super(participant);
    }
}
