package net.nevercloud.node.network.packet.serverside.server;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.network.packet.JsonPacket;
import net.nevercloud.lib.node.NodeInfo;

public class PacketSOutUpdateNodeInfo extends JsonPacket {
    public PacketSOutUpdateNodeInfo(NodeInfo nodeInfo) {
        super(14);
        this.setSimpleJsonObject(new SimpleJsonObject().append("nodeInfo", nodeInfo));
    }
}
