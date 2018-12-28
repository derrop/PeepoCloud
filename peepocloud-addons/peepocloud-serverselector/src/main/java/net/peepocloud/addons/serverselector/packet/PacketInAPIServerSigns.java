package net.peepocloud.addons.serverselector.packet;


import net.peepocloud.addons.serverselector.ServerSelectorAddon;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;
import net.peepocloud.lib.serverselector.signselector.sign.ServerSign;

import java.util.function.Consumer;

public class PacketInAPIServerSigns extends JsonPacketHandler {
    private ServerSelectorAddon serverSelectorAddon;

    public PacketInAPIServerSigns(ServerSelectorAddon serverSelectorAddon) {
        this.serverSelectorAddon = serverSelectorAddon;
    }

    @Override
    public int getId() {
        return 151;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        if(packet.getSimpleJsonObject() != null && packet.getSimpleJsonObject().contains("serverSigns"))
            this.serverSelectorAddon.saveSigns(packet.getSimpleJsonObject().getObject("serverSigns", ServerSign[].class));
    }
}
