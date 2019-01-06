package net.peepocloud.node.network.packet.out.api.user;
/*
 * Created by Mc_Ruben on 06.01.2019
 */

import net.peepocloud.lib.network.packet.serialization.array.ArraySerializationPacket;
import net.peepocloud.lib.users.User;

public class PacketOutAPIUserUpdated extends ArraySerializationPacket {
    public PacketOutAPIUserUpdated(User oldUser, User newUser) {
        super(106, oldUser, newUser);
    }
}
