package net.peepocloud.node.api.installableplugins;
/*
 * Created by Mc_Ruben on 16.01.2019
 */

import lombok.*;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;

import java.io.DataInput;
import java.io.DataOutput;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InstallablePlugin implements PacketSerializable {

    private String name;
    private String backend;

    @Override
    public void serialize(DataOutput dataOutput) throws Exception {
        dataOutput.writeUTF(name);
        dataOutput.writeUTF(backend);
    }

    @Override
    public void deserialize(DataInput dataInput) throws Exception {
        name = dataInput.readUTF();
        backend = dataInput.readUTF();
    }
}
