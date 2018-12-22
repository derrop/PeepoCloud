package net.peepocloud.node.api.event.network.node;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.node.api.event.network.NetworkEvent;
import net.peepocloud.node.api.network.NodeParticipant;

@Getter
@AllArgsConstructor
/**
 * Events that are called when something with a connected node happens
 */
public class NetworkNodeEvent extends NetworkEvent {
    private NodeParticipant participant;
}
