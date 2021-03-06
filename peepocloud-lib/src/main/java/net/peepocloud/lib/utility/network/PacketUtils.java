package net.peepocloud.lib.utility.network;


import io.netty.buffer.ByteBuf;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;
import net.peepocloud.lib.utility.SystemUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PacketUtils {

    public static <T extends PacketSerializable> T deserializeObject(DataInput dataInput, Class<? extends T> clazz) {
        if (!PacketSerializable.class.isAssignableFrom(clazz)) {
            return null;
        }
        try {
            Constructor constructor = SystemUtils.createConstructorForSerialization(clazz);
            Object obj = constructor.newInstance();
            if (!(obj instanceof PacketSerializable)) {
                return null;
            }
            T t = (T) obj;
            t.deserialize(dataInput);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void serializeObject(DataOutput dataOutput, PacketSerializable serializable) {
        try {
            serializable.serialize(dataOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends PacketSerializable> T deserializeObject(DataInput dataInput) {
        try {
            Class<? extends T> clazz = (Class<? extends T>) Class.forName(dataInput.readUTF());
            return deserializeObject(dataInput, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void serializeObjectWithClass(DataOutput dataOutput, PacketSerializable serializable) {
        try {
            dataOutput.writeUTF(serializable.getClass().getName());
            serializable.serialize(dataOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeString(ByteBuf byteBuf, String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

        writeVarInt(byteBuf, bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public static String readString(ByteBuf byteBuf) {
        return byteBuf.readBytes(readVarInt(byteBuf)).toString(StandardCharsets.UTF_8);
    }

    public static void writeUUID(ByteBuf byteBuf, UUID uuid) {
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf byteBuf) {
        return new UUID(byteBuf.readLong(), byteBuf.readLong());
    }

    public static void writeUUID(DataOutput dataOutput, UUID uuid) throws IOException {
        dataOutput.writeLong(uuid.getMostSignificantBits());
        dataOutput.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(DataInput dataInput) throws IOException {
        return new UUID(dataInput.readLong(), dataInput.readLong());
    }

    public static void writeBytes(DataOutput dataOutput, byte[] bytes) throws IOException {
        dataOutput.writeInt(bytes.length);
        for (byte aByte : bytes) {
            dataOutput.writeByte(aByte);
        }
    }

    public static byte[] readBytes(DataInput dataInput) throws IOException {
        byte[] bytes = new byte[dataInput.readInt()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = dataInput.readByte();
        }
        return bytes;
    }

    public static void writeVarInt(ByteBuf byteBuf, int value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            byteBuf.writeByte(temp);
        } while (value != 0);
    }

    public static int readVarInt(ByteBuf byteBuf) {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = byteBuf.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public static void writeVarLong(ByteBuf byteBuf, long value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            byteBuf.writeByte(temp);
        } while (value != 0);
    }

    public static long readVarLong(ByteBuf byteBuf) {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = byteBuf.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }


}
