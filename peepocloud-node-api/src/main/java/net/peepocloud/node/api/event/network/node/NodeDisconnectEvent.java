package net.peepocloud.node.api.event.network.node;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.Getter;
import net.peepocloud.node.api.network.NodeParticipant;

@Getter
/**
 * Called when a node is disconnected
 */
public class NodeDisconnectEvent extends NetworkNodeEvent {
    public NodeDisconnectEvent(NodeParticipant participant) {
        super(participant);
    }
}
