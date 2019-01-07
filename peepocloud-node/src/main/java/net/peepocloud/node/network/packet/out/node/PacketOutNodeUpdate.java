package net.peepocloud.node.network.packet.out.node;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import net.peepocloud.lib.network.packet.Packet;

import java.io.DataInput;
import java.io.DataOutput;

public class PacketOutNodeUpdate extends Packet {
    public PacketOutNodeUpdate() {
        super(-3);
    }

    @Override
    public void write(DataOutput dataOutput) throws Exception {
    }

    @Override
    public void read(DataInput dataInput) throws Exception {
    }
}
