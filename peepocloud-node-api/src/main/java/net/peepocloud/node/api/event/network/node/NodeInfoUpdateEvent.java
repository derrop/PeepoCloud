package net.peepocloud.node.api.event.network.node;
/*
 * Created by Mc_Ruben on 04.12.2018
 */

import lombok.Getter;
import net.peepocloud.lib.node.NodeInfo;
import net.peepocloud.node.api.event.network.NetworkEvent;
import net.peepocloud.node.api.network.ClientNode;

@Getter
/**
 * Called when a node (an other node than this instance) is updated
 */
public class NodeInfoUpdateEvent extends NetworkEvent {
    public NodeInfoUpdateEvent(ClientNode clientNode, NodeInfo newNodeInfo, NodeInfo oldNodeInfo) {
        this.oldNodeInfo = oldNodeInfo;
        this.newNodeInfo = newNodeInfo;
        this.clientNode = clientNode;
    }

    private ClientNode clientNode;
    private NodeInfo newNodeInfo, oldNodeInfo;
}
