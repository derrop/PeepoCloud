package net.peepocloud.lib.utility.network;
/*
 * Created by Mc_Ruben on 04.01.2019
 */

import lombok.*;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;

import java.io.*;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PacketSerializableWrapper implements PacketSerializable {

    private Serializable serializable;

    @Override
    public void serialize(DataOutput dataOutput) throws Exception {
        ObjectOutput objectOutput = new ObjectOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                dataOutput.writeByte(b);
            }
        });
        objectOutput.writeObject(this.serializable);
        objectOutput.close();
    }

    @Override
    public void deserialize(DataInput dataInput) throws Exception {
        ObjectInput objectInput = new ObjectInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                return dataInput.readByte();
            }
        });
        this.serializable = (Serializable) objectInput.readObject();
        objectInput.close();
    }
}
