package net.nevercloud.lib.network.packet;


import io.netty.buffer.ByteBuf;
import net.nevercloud.lib.json.SimpleJsonObject;

public class JsonPacket extends Packet {
    private SimpleJsonObject simpleJsonObject;

    public JsonPacket(int id) {
        super(id);
    }

    public void write(ByteBuf byteBuf) {
        super.writeString(byteBuf, this.simpleJsonObject.toJson());
    }

    public void read(ByteBuf byteBuf) {
        this.simpleJsonObject = new SimpleJsonObject(super.readString(byteBuf));
    }

    public void setSimpleJsonObject(SimpleJsonObject simpleJsonObject) {
        this.simpleJsonObject = simpleJsonObject;
    }

    public SimpleJsonObject getSimpleJsonObject() {
        return simpleJsonObject;
    }
}
