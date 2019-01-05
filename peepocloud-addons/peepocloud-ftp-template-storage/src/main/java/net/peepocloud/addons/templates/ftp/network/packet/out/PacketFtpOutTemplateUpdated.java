package net.peepocloud.addons.templates.ftp.network.packet.out;
/*
 * Created by Mc_Ruben on 05.01.2019
 */

import lombok.Getter;
import net.peepocloud.lib.network.packet.Packet;

import java.io.DataInput;
import java.io.DataOutput;

@Getter
public class PacketFtpOutTemplateUpdated extends Packet {

    private String group, template;

    public PacketFtpOutTemplateUpdated(String group, String template) {
        super(340);
        this.group = group;
        this.template = template;
    }

    @Override
    public void write(DataOutput dataOutput) throws Exception {
        dataOutput.writeUTF(group);
        dataOutput.writeUTF(template);
    }

    @Override
    public void read(DataInput dataInput) throws Exception {
        group = dataInput.readUTF();
        template = dataInput.readUTF();
    }
}
