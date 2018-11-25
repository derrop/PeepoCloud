package net.nevercloud.node.network.packet.out;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.node.NodeInfo;

public class PacketOutUpdateNodeInfo extends JsonPacket {
    public PacketOutUpdateNodeInfo(NodeInfo nodeInfo) {
        super(14);
        this.setSimpleJsonObject(new SimpleJsonObject().append("nodeInfo", nodeInfo));
    }
}
