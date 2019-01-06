package net.peepocloud.node.network.packet.in.api.user;
/*
 * Created by Mc_Ruben on 06.01.2019
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.users.User;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.user.UserCreateEvent;
import net.peepocloud.node.api.event.user.UserDeleteEvent;

import java.util.function.Consumer;

public class PacketInAPIUserDeleted implements PacketHandler<SerializationPacket> {
    @Override
    public int getId() {
        return 105;
    }

    @Override
    public Class<SerializationPacket> getPacketClass() {
        return SerializationPacket.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, SerializationPacket packet, Consumer<Packet> queryResponse) {
        PeepoCloudNode.getInstance().getEventManager().callEvent(new UserDeleteEvent((User) packet.getSerializable()));
    }
}
