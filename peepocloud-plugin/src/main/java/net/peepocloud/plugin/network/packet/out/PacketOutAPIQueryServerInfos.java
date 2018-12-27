package net.peepocloud.plugin.network.packet.out;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;

public class PacketOutAPIQueryServerInfos extends JsonPacket {

    public PacketOutAPIQueryServerInfos(String groupName, boolean onlyStarted) {
        super(200);
        super.setSimpleJsonObject(new SimpleJsonObject());
        if(groupName != null)
            super.getSimpleJsonObject().append("group", groupName);
        super.getSimpleJsonObject().append("onlyStarted", onlyStarted);
    }

    public PacketOutAPIQueryServerInfos(String groupName) {
        this(groupName, false);
    }

    public PacketOutAPIQueryServerInfos(boolean onlyStarted) {
        this(null, onlyStarted);
    }

    public PacketOutAPIQueryServerInfos() {
        this(false);
    }

}
