package net.peepocloud.lib.network.packet.serialization;
/*
 * Created by Mc_Ruben on 25.12.2018
 */

import java.io.DataInput;
import java.io.DataOutput;

public interface PacketSerializable {

    void serialize(DataOutput dataOutput) throws Exception;

    void deserialize(DataInput dataInput) throws Exception;

}
