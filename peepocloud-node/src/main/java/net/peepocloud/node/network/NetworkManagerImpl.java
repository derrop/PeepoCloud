package net.peepocloud.node.network;
/*
 * Created by Mc_Ruben on 05.01.2019
 */

import lombok.AllArgsConstructor;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.network.NetworkManager;
import net.peepocloud.node.network.packet.out.PacketOutSendPacket;

import java.util.Collections;

@AllArgsConstructor
public class NetworkManagerImpl implements NetworkManager {

    private PeepoCloudNode node;

    @Override
    public void sendPacketToNodes(Packet packet) {
        this.node.getConnectedNodes().values().forEach(clientNode -> clientNode.sendPacket(packet));
    }

    @Override
    public void sendPacketToServersAndProxiesOnThisNode(Packet packet) {
        this.sendPacketToServersOnThisNode(packet);
        this.sendPacketToProxiesOnThisNode(packet);
    }

    @Override
    public void sendPacketToServersOnThisNode(Packet packet) {
        this.node.getServersOnThisNode().values().forEach(minecraftServerParticipant -> minecraftServerParticipant.sendPacket(packet));
    }

    @Override
    public void sendPacketToProxiesOnThisNode(Packet packet) {
        this.node.getProxiesOnThisNode().values().forEach(bungeeCordParticipant -> bungeeCordParticipant.sendPacket(packet));
    }

    @Override
    public void sendPacketToServersAndProxies(Packet packet) {
        this.sendPacketToServersAndProxiesOnThisNode(packet);
        this.sendPacketToNodes(new PacketOutSendPacket(packet));
    }

    @Override
    public void sendPacketToServers(Packet packet) {
        this.sendPacketToServersOnThisNode(packet);
        this.sendPacketToNodes(new PacketOutSendPacket(Collections.singletonList(new PacketOutSendPacket.PacketReceiver(NetworkComponentType.MINECRAFT_SERVER, null)), packet));
    }

    @Override
    public void sendPacketToProxies(Packet packet) {
        this.sendPacketToProxiesOnThisNode(packet);
        this.sendPacketToNodes(new PacketOutSendPacket(Collections.singletonList(new PacketOutSendPacket.PacketReceiver(NetworkComponentType.BUNGEECORD, null)), packet));
    }

    @Override
    public void sendPacketToAllOnThisNode(Packet packet) {
        this.sendPacketToNodes(packet);
        this.sendPacketToServersAndProxies(packet);
    }

    @Override
    public void sendPacketToAll(Packet packet) {
        this.sendPacketToAllOnThisNode(packet);
        this.sendPacketToNodes(new PacketOutSendPacket(packet));
    }
}
