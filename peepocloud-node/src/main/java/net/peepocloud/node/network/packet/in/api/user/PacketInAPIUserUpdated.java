package net.peepocloud.node.network.packet.in.api.user;
/*
 * Created by Mc_Ruben on 06.01.2019
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.network.packet.serialization.array.ArrayPacketSerializable;
import net.peepocloud.lib.users.User;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.user.UserUpdateEvent;

import java.util.function.Consumer;

public class PacketInAPIUserUpdated implements PacketHandler<SerializationPacket> {
    @Override
    public int getId() {
        return 106;
    }

    @Override
    public Class<SerializationPacket> getPacketClass() {
        return SerializationPacket.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, SerializationPacket packet, Consumer<Packet> queryResponse) {
        User[] users = (User[]) ArrayPacketSerializable.getSerializablesFromArraySerializable(packet.getSerializable());
        PeepoCloudNode.getInstance().getEventManager().callEvent(new UserUpdateEvent(users[0], users[1]));
    }
}
