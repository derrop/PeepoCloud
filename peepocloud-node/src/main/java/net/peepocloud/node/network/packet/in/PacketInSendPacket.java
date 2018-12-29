package net.peepocloud.node.network.packet.in;
/*
 * Created by Mc_Ruben on 27.12.2018
 */

import lombok.*;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.network.NodeParticipant;
import net.peepocloud.node.network.packet.out.PacketOutSendPacket;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PacketInSendPacket implements PacketHandler<PacketOutSendPacket> {
    @Override
    public int getId() {
        return 40;
    }

    @Override
    public Class<PacketOutSendPacket> getPacketClass() {
        return PacketOutSendPacket.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, PacketOutSendPacket packet, Consumer<Packet> queryResponse) {
        if (!(networkParticipant instanceof NodeParticipant))
            return;
        if (packet.getPacket() == null)
            return;
        if (packet.getTargets().isEmpty()) {
            PeepoCloudNode.getInstance().sendPacketToServersAndProxies(packet.getPacket());
        } else {
            for (PacketOutSendPacket.PacketReceiver target : packet.getTargets()) {
                if (target.getName() == null) {
                    switch (target.getType()) {
                        case BUNGEECORD:
                            PeepoCloudNode.getInstance().getProxiesOnThisNode().values().forEach(bungeeCordParticipant -> {
                                bungeeCordParticipant.sendPacket(packet.getPacket());
                            });
                            break;
                        case MINECRAFT_SERVER:
                            PeepoCloudNode.getInstance().getServersOnThisNode().values().forEach(minecraftServerParticipant -> {
                                minecraftServerParticipant.sendPacket(packet.getPacket());
                            });
                            break;
                    }
                } else {
                    NetworkPacketSender participant = null;
                    switch (target.getType()) {
                        case BUNGEECORD:
                            participant = PeepoCloudNode.getInstance().getProxiesOnThisNode().get(target.getName());
                            break;
                        case MINECRAFT_SERVER:
                            participant = PeepoCloudNode.getInstance().getServersOnThisNode().get(target.getName());
                            break;
                    }
                    if (participant != null) {
                        participant.sendPacket(packet.getPacket());
                    }
                }
            }
        }
    }
}
