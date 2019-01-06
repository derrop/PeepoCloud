package net.peepocloud.lib.network.packet.serialization.array;
/*
 * Created by Mc_Ruben on 06.01.2019
 */

import net.peepocloud.lib.network.packet.serialization.PacketSerializable;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;

public class ArraySerializationPacket extends SerializationPacket {

    public ArraySerializationPacket(int id, PacketSerializable... serializables) {
        super(id, new ArrayPacketSerializable(serializables));
    }

    public ArraySerializationPacket(int id) {
        super(id);
    }
}
