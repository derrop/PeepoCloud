package net.nevercloud.lib.network.packet;


import io.netty.buffer.ByteBuf;

public abstract class Packet {
    private int id;

    public Packet(int id) {
        this.id = id;
    }

    public abstract void write(ByteBuf byteBuf);
    public abstract void read(ByteBuf byteBuf);

    protected void writeString(ByteBuf byteBuf, String string) {
        byte[] bytes = string.getBytes();

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    protected String readString(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readInt()];
        byteBuf.readBytes(bytes);

        return new String(bytes);
    }

    public int getId() {
        return id;
    }
}
