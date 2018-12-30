package net.peepocloud.node.network.packet.out.group;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import lombok.Getter;
import net.peepocloud.lib.network.packet.Packet;

import java.io.DataInput;
import java.io.DataOutput;

public class PacketOutBungeeGroupDeleted extends Packet {

    @Getter
    private String name;

    public PacketOutBungeeGroupDeleted(String name) {
        super(3);
        this.name = name;
    }

    public PacketOutBungeeGroupDeleted(int id) {
        super(id);
    }

    @Override
    public void write(DataOutput dataOutput) throws Exception {
        dataOutput.writeUTF(this.name);
    }

    @Override
    public void read(DataInput dataInput) throws Exception {
        this.name = dataInput.readUTF();
    }
}
