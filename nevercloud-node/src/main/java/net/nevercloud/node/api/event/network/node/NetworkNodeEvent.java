package net.nevercloud.node.api.event.network.node;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.*;
import net.nevercloud.node.api.event.network.NetworkEvent;
import net.nevercloud.node.network.participant.NodeParticipant;

@Getter
@AllArgsConstructor
public class NetworkNodeEvent extends NetworkEvent {
    private NodeParticipant participant;
}
