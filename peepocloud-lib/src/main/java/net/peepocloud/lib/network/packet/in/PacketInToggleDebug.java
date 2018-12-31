package net.peepocloud.lib.network.packet.in;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.AbstractPeepoCloudAPI;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;

import java.util.function.Consumer;

public class PacketInToggleDebug extends JsonPacketHandler {
    @Override
    public int getId() {
        return -10;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        AbstractPeepoCloudAPI.getInstance().setDebuggingOnThisComponent(packet.getSimpleJsonObject().getBoolean("debug"));
    }
}
