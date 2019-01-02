package net.peepocloud.addons.serverselector.packet;


import net.peepocloud.addons.serverselector.ServerSelectorAddon;
import net.peepocloud.lib.config.json.SimpleJsonObject;
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
        SimpleJsonObject simpleJsonObject = packet.getSimpleJsonObject();
        System.out.println(simpleJsonObject.toPrettyJson());
        if(simpleJsonObject != null && simpleJsonObject.contains("serverSigns") && simpleJsonObject.contains("group"))
            this.serverSelectorAddon.saveSigns(simpleJsonObject.getObject("serverSigns", ServerSign[].class), simpleJsonObject.getString("group"));
    }
}
