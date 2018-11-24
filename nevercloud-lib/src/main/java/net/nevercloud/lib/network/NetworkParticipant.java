package net.nevercloud.lib.network;

import io.netty.channel.Channel;
import net.nevercloud.lib.network.packet.Packet;
import java.net.InetSocketAddress;

public class NetworkParticipant {
    private String name;
    protected Channel channel;
    protected long connectedAt;

    public NetworkParticipant(String name, Channel channel, long connectedAt) {
        this.name = name;
        this.channel = channel;
        this.connectedAt = connectedAt;
    }

    public NetworkParticipant(String name, Channel channel) {
        this(name, channel, System.currentTimeMillis());
    }

    public String getName() {
        return name;
    }

    public void sendPacket(Packet packet) {
        if (this.isConnected())
            this.channel.writeAndFlush(packet);
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    public Channel getChannel() {
        return channel;
    }

    public String getAddress() {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
    }

    public long getConnectedAt() {
        return connectedAt;
    }
}
