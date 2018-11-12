package net.nevercloud.lib.network.packet;


import io.netty.buffer.ByteBuf;
import net.nevercloud.lib.conf.json.SimpleJsonObject;
import net.nevercloud.lib.utility.network.PacketUtils;

public class JsonPacket extends Packet {
    private SimpleJsonObject simpleJsonObject;

    public JsonPacket(int id) {
        super(id);
    }

    public void write(ByteBuf byteBuf) {
        PacketUtils.writeString(byteBuf, this.simpleJsonObject.toJson());
    }

    public void read(ByteBuf byteBuf) {
        this.simpleJsonObject = new SimpleJsonObject(PacketUtils.readString(byteBuf));
    }

    public void setSimpleJsonObject(SimpleJsonObject simpleJsonObject) {
        this.simpleJsonObject = simpleJsonObject;
    }

    public SimpleJsonObject getSimpleJsonObject() {
        return simpleJsonObject;
    }
}
