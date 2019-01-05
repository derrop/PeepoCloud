package net.peepocloud.addons.templates.ftp.network.packet.in;
/*
 * Created by Mc_Ruben on 05.01.2019
 */

import net.peepocloud.addons.templates.ftp.FtpTemplatesAddon;
import net.peepocloud.addons.templates.ftp.network.packet.out.PacketFtpOutTemplateUpdated;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;

import java.util.function.Consumer;

public class PacketFtpInTemplateUpdated implements PacketHandler<PacketFtpOutTemplateUpdated> {
    @Override
    public int getId() {
        return 340;
    }

    @Override
    public Class<PacketFtpOutTemplateUpdated> getPacketClass() {
        return PacketFtpOutTemplateUpdated.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, PacketFtpOutTemplateUpdated packet, Consumer<Packet> queryResponse) {
        FtpTemplatesAddon.getInstance().getTemplateStorage().updateInCache(packet.getGroup(), packet.getTemplate());
    }
}
