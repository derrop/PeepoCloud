package net.peepocloud.lib.network.packet.serialization;
/*
 * Created by Mc_Ruben on 25.12.2018
 */

import java.io.*;
import java.lang.reflect.Field;

public interface ReflectivePacketSerializable extends PacketSerializable, Serializable {
    @Override
    default void serialize(DataOutput dataOutput) throws Exception {
        ObjectOutput objectOutput = new ObjectOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                dataOutput.write(b);
            }
        });
        objectOutput.writeObject(this);
        objectOutput.close();
    }

    @Override
    default void deserialize(DataInput dataInput) throws Exception {
        ObjectInput objectInput = new ObjectInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                return dataInput.readByte();
            }
        });
        Object obj = objectInput.readObject();
        Field[] thisFields = getClass().getDeclaredFields();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0; i < thisFields.length; i++) {
            Field thisField = thisFields[i];
            Field field = fields[i];
            if (!thisField.isAccessible()) {
                thisField.setAccessible(true);
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            thisField.set(this, field.get(obj));
        }
    }
}
