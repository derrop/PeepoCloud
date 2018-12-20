package net.peepocloud.lib.network.auth;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;

public class PacketOutAuth extends JsonPacket {

    public PacketOutAuth(Auth auth) {
        super(-1);
        setSimpleJsonObject(new SimpleJsonObject(SimpleJsonObject.GSON.toJsonTree(auth).getAsJsonObject()));
    }
}
