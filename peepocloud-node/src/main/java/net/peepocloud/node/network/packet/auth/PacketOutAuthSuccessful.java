package net.peepocloud.node.network.packet.auth;
/*
 * Created by Mc_Ruben on 03.01.2019
 */

import net.peepocloud.lib.network.packet.Packet;

import java.io.DataInput;
import java.io.DataOutput;

public class PacketOutAuthSuccessful extends Packet {
    public PacketOutAuthSuccessful(int id) {
        super(id);
    }

    public PacketOutAuthSuccessful() {
        super(-2);
    }

    @Override
    public void write(DataOutput dataOutput) throws Exception {
    }

    @Override
    public void read(DataInput dataInput) throws Exception {
    }
}
