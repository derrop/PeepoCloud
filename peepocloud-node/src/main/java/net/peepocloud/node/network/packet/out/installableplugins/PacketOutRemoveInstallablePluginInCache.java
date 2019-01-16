package net.peepocloud.node.network.packet.out.installableplugins;
/*
 * Created by Mc_Ruben on 16.01.2019
 */

import lombok.Getter;
import net.peepocloud.lib.network.packet.Packet;

import java.io.DataInput;
import java.io.DataOutput;

@Getter
public class PacketOutRemoveInstallablePluginInCache extends Packet {
    private String name;

    public PacketOutRemoveInstallablePluginInCache(String name) {
        super(57);
        this.name = name;
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
