package net.peepocloud.plugin.network.packet.out.query;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;

public class PacketOutAPIQueryProxyInfos extends JsonPacket {

    public PacketOutAPIQueryProxyInfos(String groupName, boolean onlyStarted) {
        super(201);
        super.setSimpleJsonObject(new SimpleJsonObject());
        if(groupName != null)
            super.getSimpleJsonObject().append("group", groupName);
        super.getSimpleJsonObject().append("onlyStarted", onlyStarted);
    }

    public PacketOutAPIQueryProxyInfos(String groupName) {
        this(groupName, false);
    }

    public PacketOutAPIQueryProxyInfos(boolean onlyStarted) {
        this(null, onlyStarted);
    }

    public PacketOutAPIQueryProxyInfos() {
        this(false);
    }


}
