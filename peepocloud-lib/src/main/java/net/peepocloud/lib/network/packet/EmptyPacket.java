package net.peepocloud.lib.network.packet;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import java.io.DataInput;
import java.io.DataOutput;

public class EmptyPacket extends Packet {
    public EmptyPacket(int id) {
        super(id);
    }

    @Override
    public void write(DataOutput dataOutput) throws Exception {
    }

    @Override
    public void read(DataInput dataInput) throws Exception {
    }
}
