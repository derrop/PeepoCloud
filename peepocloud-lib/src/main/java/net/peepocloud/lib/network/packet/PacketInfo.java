package net.peepocloud.lib.network.packet;

import net.peepocloud.lib.network.packet.handler.PacketHandler;

public class PacketInfo {
    private int id;
    private Class<? extends Packet> packetClass;
    private PacketHandler packetHandler;

    public PacketInfo(int id, Class<? extends Packet> packetClass, PacketHandler packetHandler) {
        this.id = id;
        this.packetClass = packetClass;
        this.packetHandler = packetHandler;
    }

    public int getId() {
        return id;
    }

    public Class<? extends Packet> getPacketClass() {
        return packetClass;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }
}
