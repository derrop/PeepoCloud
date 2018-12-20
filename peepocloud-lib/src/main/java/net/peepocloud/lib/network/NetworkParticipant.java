package net.peepocloud.lib.network;

import io.netty.channel.Channel;
import net.peepocloud.api.network.NetworkPacketSender;
import net.peepocloud.api.network.packet.Packet;

import java.net.InetSocketAddress;

public class NetworkParticipant implements NetworkPacketSender {
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

    @Override
    public void sendPacket(Packet packet) {
        if (this.isConnected())
            this.channel.writeAndFlush(packet);
    }

    @Override
    public void sendPacketSync(Packet packet) {
        if (this.isConnected())
            this.channel.writeAndFlush(packet).syncUninterruptibly();
    }

    @Override
    public void close() {
        if (this.channel != null)
            this.channel.close();
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    public Channel getChannel() {
        return channel;
    }

    public String getAddress() {
        if (this.channel == null)
            return null;
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
    }

    public long getConnectedAt() {
        return connectedAt;
    }
}
