package net.nevercloud.lib.network.packet;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public abstract class Packet {
    private int id;
    private UUID queryUUID;

    public Packet(int id) {
        this.id = id;
    }

    public abstract void write(ByteBuf byteBuf);
    public abstract void read(ByteBuf byteBuf);

    public int getId() {
        return id;
    }

    public UUID getQueryUUID() {
        return queryUUID;
    }

    void setQueryUUID(UUID queryUUID) {
        this.queryUUID = queryUUID;
    }
}
