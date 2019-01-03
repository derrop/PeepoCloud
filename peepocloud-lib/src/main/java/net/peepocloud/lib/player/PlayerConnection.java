package net.peepocloud.lib.player;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import lombok.*;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;

import java.io.DataInput;
import java.io.DataOutput;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class PlayerConnection implements PacketSerializable {
    private String ip;
    private int port;
    private int protocolVersion;

    @Override
    public void serialize(DataOutput dataOutput) throws Exception {
        dataOutput.writeUTF(ip);
        dataOutput.writeInt(port);
        dataOutput.writeInt(protocolVersion);
    }

    @Override
    public void deserialize(DataInput dataInput) throws Exception {
        ip = dataInput.readUTF();
        port = dataInput.readInt();
        protocolVersion = dataInput.readInt();
    }
}
