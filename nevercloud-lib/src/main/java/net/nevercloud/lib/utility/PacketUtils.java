package net.nevercloud.lib.utility;


import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PacketUtils {

    public static void writeString(ByteBuf byteBuf, String string) {
        byte[] bytes = string.getBytes();

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public static String readString(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readInt()];
        byteBuf.readBytes(bytes);

        return new String(bytes);
    }

    public static void writeUUID(ByteBuf byteBuf, UUID uuid) {
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf byteBuf) {
        return new UUID(byteBuf.readLong(), byteBuf.readLong());
    }


}
