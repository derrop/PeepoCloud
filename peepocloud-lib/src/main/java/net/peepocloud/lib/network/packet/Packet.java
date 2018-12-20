package net.peepocloud.lib.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.UUID;

public abstract class Packet {
    private int id;
    private UUID queryUUID;

    public Packet(int id) {
        this.id = id;
    }

    public abstract void write(DataOutput dataOutput) throws Exception;
    public abstract void read(DataInput dataInput) throws Exception;

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
