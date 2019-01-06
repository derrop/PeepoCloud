package net.peepocloud.lib.network.packet.serialization.array;
/*
 * Created by Mc_Ruben on 06.01.2019
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;
import net.peepocloud.lib.utility.network.PacketUtils;

import java.io.DataInput;
import java.io.DataOutput;

@AllArgsConstructor
@Getter
public class ArrayPacketSerializable implements PacketSerializable {

    private PacketSerializable[] serializables;

    @Override
    public void serialize(DataOutput dataOutput) throws Exception {
        dataOutput.writeInt(serializables.length);
        for (PacketSerializable serializable : serializables) {
            PacketUtils.serializeObjectWithClass(dataOutput, serializable);
        }
    }

    @Override
    public void deserialize(DataInput dataInput) throws Exception {
        serializables = new PacketSerializable[dataInput.readInt()];
        for (int i = 0; i < serializables.length; i++) {
            serializables[i] = PacketUtils.deserializeObject(dataInput);
        }
    }

    public static boolean isArraySerializable(Class<?> serializable) {
        return ArrayPacketSerializable.class.isAssignableFrom(serializable) || serializable.isArray();
    }

    public static PacketSerializable[] getSerializablesFromArraySerializable(Object serializable) {
        if (serializable instanceof ArrayPacketSerializable)
            return ((ArrayPacketSerializable) serializable).serializables;
        if (serializable.getClass().isArray())
            return (PacketSerializable[]) serializable;
        return null;
    }
}
