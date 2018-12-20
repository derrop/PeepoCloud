package net.peepocloud.node.network.packet.out;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.node.NodeInfo;

public class PacketOutUpdateNodeInfo extends JsonPacket {
    public PacketOutUpdateNodeInfo(NodeInfo nodeInfo) {
        super(14);
        this.setSimpleJsonObject(new SimpleJsonObject().append("nodeInfo", nodeInfo));
    }
}
