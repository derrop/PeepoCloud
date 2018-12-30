package net.peepocloud.node.network.packet.out.node;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import lombok.Getter;
import net.peepocloud.lib.network.packet.Packet;

import java.io.DataInput;
import java.io.DataOutput;

public class PacketOutExecuteNodeCommand extends Packet {
    @Getter
    private String command;

    public PacketOutExecuteNodeCommand(String command) {
        super(5);
        this.command = command;
    }

    public PacketOutExecuteNodeCommand(int id) {
        super(id);
    }

    @Override
    public void write(DataOutput dataOutput) throws Exception {
        dataOutput.writeUTF(this.command);
    }

    @Override
    public void read(DataInput dataInput) throws Exception {
        this.command = dataInput.readUTF();
    }
}
