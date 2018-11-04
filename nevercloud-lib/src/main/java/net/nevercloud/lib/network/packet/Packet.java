package net.nevercloud.lib.network.packet;


import io.netty.buffer.ByteBuf;

public abstract class Packet {
    private int id;

    public Packet(int id) {
        this.id = id;
    }

    public abstract void write(ByteBuf byteBuf);
    public abstract void read(ByteBuf byteBuf);

    public int getId() {
        return id;
    }
}
