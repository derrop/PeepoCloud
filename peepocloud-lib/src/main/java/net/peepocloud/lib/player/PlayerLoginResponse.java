package net.peepocloud.lib.player;
/*
 * Created by Mc_Ruben on 03.01.2019
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;

import java.io.DataInput;
import java.io.DataOutput;

@Getter
@AllArgsConstructor
public class PlayerLoginResponse implements PacketSerializable {

    private boolean allowed;
    private String kickReason;

    @Override
    public void serialize(DataOutput dataOutput) throws Exception {
        dataOutput.writeBoolean(allowed);
        if (!allowed)
            dataOutput.writeUTF(kickReason);
    }

    @Override
    public void deserialize(DataInput dataInput) throws Exception {
        if (!(allowed = dataInput.readBoolean())) {
            kickReason = dataInput.readUTF();
        }
    }
}
