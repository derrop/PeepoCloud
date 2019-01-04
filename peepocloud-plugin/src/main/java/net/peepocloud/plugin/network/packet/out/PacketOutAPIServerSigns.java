package net.peepocloud.plugin.network.packet.out;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.serverselector.signselector.sign.ServerSign;

import java.util.ArrayList;
import java.util.Collection;

public class PacketOutAPIServerSigns extends JsonPacket {


    public PacketOutAPIServerSigns(Collection<ServerSign> serverSigns, String group) {
        super(151);
        super.setSimpleJsonObject(new SimpleJsonObject().append("group", group).append("serverSigns", new ArrayList<>(serverSigns)));
    }

}
