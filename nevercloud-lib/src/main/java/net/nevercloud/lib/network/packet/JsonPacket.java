package net.nevercloud.lib.network.packet;


import net.nevercloud.lib.config.json.SimpleJsonObject;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class JsonPacket extends Packet {
    private SimpleJsonObject simpleJsonObject;

    public JsonPacket(int id) {
        super(id);
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.simpleJsonObject.toJson());
    }

    public void read(DataInput dataInput) throws IOException {
        this.simpleJsonObject = new SimpleJsonObject(dataInput.readUTF());
    }

    public void setSimpleJsonObject(SimpleJsonObject simpleJsonObject) {
        this.simpleJsonObject = simpleJsonObject;
    }

    public SimpleJsonObject getSimpleJsonObject() {
        return simpleJsonObject;
    }
}
