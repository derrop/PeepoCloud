package net.peepocloud.node.api.event.network.node;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.*;
import net.peepocloud.api.event.network.NetworkEvent;
import net.peepocloud.node.network.participant.NodeParticipant;

@Getter
@AllArgsConstructor
/**
 * Events that are called when something with a connected node happens
 */
public class NetworkNodeEvent extends NetworkEvent {
    private NodeParticipant participant;
}
