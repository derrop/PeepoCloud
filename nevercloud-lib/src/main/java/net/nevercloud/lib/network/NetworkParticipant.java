package net.nevercloud.lib.network;


import io.netty.channel.Channel;
import net.nevercloud.lib.network.packet.Packet;

import java.net.InetSocketAddress;

public class NetworkParticipant {
    private Channel channel;
    private String address;
    private long connectedAt;

    public NetworkParticipant(Channel channel, long connectedAt) {
        this.channel = channel;
        this.address = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        this.connectedAt = connectedAt;
    }

    public NetworkParticipant(Channel channel) {
        this(channel, System.currentTimeMillis());
    }

    public void sendPacket(Packet packet) {
        this.channel.writeAndFlush(packet);
    }

    public Channel getChannel() {
        return channel;
    }

    public String getAddress() {
        return address;
    }

    public long getConnectedAt() {
        return connectedAt;
    }
}
