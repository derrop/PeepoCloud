package net.peepocloud.node.network.packet.out.server.process.info;
/*
 * Created by Mc_Ruben on 04.01.2019
 */

import lombok.Getter;
import net.peepocloud.lib.network.packet.Packet;

import java.io.DataInput;
import java.io.DataOutput;

public class PacketOutQueryProcessInfo extends Packet {
    @Getter
    private int pid;

    public PacketOutQueryProcessInfo(int pid) {
        super(23);
        this.pid = pid;
    }

    @Override
    public void write(DataOutput dataOutput) throws Exception {
        dataOutput.writeInt(pid);
    }

    @Override
    public void read(DataInput dataInput) throws Exception {
        pid = dataInput.readInt();
    }
}
