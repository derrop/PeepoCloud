package net.peepocloud.lib.player;
/*
 * Created by Mc_Ruben on 03.01.2019
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;

import java.io.DataInput;
import java.io.DataOutput;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PeepoClientSettings implements PacketSerializable {

    private String locale;
    private byte viewDistance;
    private int chatFlags;
    private boolean chatColours;
    private PeepoPlayerSkinConfiguration skinParts;
    private int mainHand;

    @Override
    public void serialize(DataOutput dataOutput) throws Exception {
        dataOutput.writeUTF(locale);
        dataOutput.writeByte(viewDistance);
        dataOutput.writeInt(chatFlags);
        dataOutput.writeBoolean(chatColours);
        dataOutput.writeByte(skinParts.toByte());
        dataOutput.writeInt(mainHand);
    }

    @Override
    public void deserialize(DataInput dataInput) throws Exception {
        locale = dataInput.readUTF();
        viewDistance = dataInput.readByte();
        chatFlags = dataInput.readInt();
        chatColours = dataInput.readBoolean();
        skinParts = PeepoPlayerSkinConfiguration.fromByte(dataInput.readByte());
        mainHand = dataInput.readInt();
    }
}
