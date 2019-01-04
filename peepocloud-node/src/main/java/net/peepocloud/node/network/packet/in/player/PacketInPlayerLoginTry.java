package net.peepocloud.node.network.packet.in.player;
/*
 * Created by Mc_Ruben on 03.01.2019
 */

import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.lib.player.PlayerLoginResponse;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.player.PlayerLoginEvent;
import net.peepocloud.node.api.event.player.PlayerPreLoginEvent;
import net.peepocloud.node.api.network.BungeeCordParticipant;
import net.peepocloud.node.network.participant.BungeeCordParticipantImpl;

import java.util.function.Consumer;

public class PacketInPlayerLoginTry implements PacketHandler<SerializationPacket> {
    @Override
    public int getId() {
        return 60;
    }

    @Override
    public Class<SerializationPacket> getPacketClass() {
        return SerializationPacket.class;
    }

    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, SerializationPacket packet, Consumer<Packet> queryResponse) { //TODO messages configurable
        if (!(networkParticipant instanceof BungeeCordParticipant) || !(packet.getSerializable() instanceof PeepoPlayer))
            return;

        BungeeCordParticipantImpl bungee = (BungeeCordParticipantImpl) networkParticipant;
        PeepoPlayer player = (PeepoPlayer) packet.getSerializable();

        /*if (PeepoCloudNode.getInstance().getOnlinePlayers().containsKey(player.getUniqueId())) { marked as a comment for testing
            queryResponse.accept(new SerializationPacket(99999, new PlayerLoginResponse(false, "§cYou are already connected to the network")));
            return;
        }*/

        PlayerPreLoginEvent preLoginEvent = new PlayerPreLoginEvent(player);
        if (PeepoCloudNode.getInstance().getEventManager().callEvent(preLoginEvent).isCancelled()) {
            String reason = preLoginEvent.getCancelReason() != null ? preLoginEvent.getCancelReason() : "no reason specified";
            queryResponse.accept(new SerializationPacket(99999, new PlayerLoginResponse(false, reason)));
            return;
        }

        BungeeCordProxyInfo proxyInfo = bungee.getProxyInfo();
        proxyInfo.getPlayers().put(player.getUniqueId(), player.getName());
        PeepoCloudNode.getInstance().updateProxyInfo(proxyInfo);

        PeepoCloudNode.getInstance().updatePlayer(player);

        queryResponse.accept(new SerializationPacket(99999, new PlayerLoginResponse(true, null)));

        PeepoCloudNode.getInstance().getEventManager().callEvent(new PlayerLoginEvent(player));
    }
}
