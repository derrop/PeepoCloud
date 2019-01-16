package net.peepocloud.node.network.packet.out.installableplugins;
/*
 * Created by Mc_Ruben on 16.01.2019
 */

import lombok.Getter;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.utility.network.PacketUtils;
import net.peepocloud.node.api.installableplugins.InstallablePlugin;

import java.io.DataInput;
import java.io.DataOutput;

@Getter
public class PacketOutUpdateInstallablePluginInCache extends Packet {
    private InstallablePlugin plugin;
    private byte[] data;

    public PacketOutUpdateInstallablePluginInCache(InstallablePlugin plugin, byte[] data) {
        super(56);
        this.plugin = plugin;
        this.data = data;
    }

    @Override
    public void write(DataOutput dataOutput) throws Exception {
        plugin.serialize(dataOutput);
        PacketUtils.writeBytes(dataOutput, data);
    }

    @Override
    public void read(DataInput dataInput) throws Exception {
        plugin = new InstallablePlugin();
        plugin.deserialize(dataInput);
        data = PacketUtils.readBytes(dataInput);
    }
}
