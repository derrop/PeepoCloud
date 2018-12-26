package net.peepocloud.lib.network.packet.serialization;
/*
 * Created by Mc_Ruben on 25.12.2018
 */

import lombok.Getter;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.utility.network.PacketUtils;

import java.io.DataInput;
import java.io.DataOutput;

public class SerializationPacket extends Packet {

    @Getter
    private PacketSerializable serializable;

    public SerializationPacket(int id, PacketSerializable serializable) {
        super(id);
        this.serializable = serializable;
    }

    public SerializationPacket(int id) {
        super(id);
    }

    @Override
    public void write(DataOutput dataOutput) throws Exception {
        if (this.serializable != null) {
            dataOutput.writeUTF(this.serializable.getClass().getName());
            this.serializable.serialize(dataOutput);
        }
    }

    @Override
    public void read(DataInput dataInput) throws Exception {
        Class<?> clazz = Class.forName(dataInput.readUTF());
        if (!PacketSerializable.class.isAssignableFrom(clazz))
            return;
        this.serializable = PacketUtils.deserializeObject(dataInput, (Class<? extends PacketSerializable>) clazz);
    }
}
