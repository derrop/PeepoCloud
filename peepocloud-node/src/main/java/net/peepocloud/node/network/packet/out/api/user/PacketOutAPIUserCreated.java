package net.peepocloud.node.network.packet.out.api.user;
/*
 * Created by Mc_Ruben on 06.01.2019
 */

import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.users.User;

public class PacketOutAPIUserCreated extends SerializationPacket {
    public PacketOutAPIUserCreated(User user) {
        super(104, user);
    }
}
