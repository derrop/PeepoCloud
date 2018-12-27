package net.peepocloud.node.network.packet.out;
/*
 * Created by Mc_Ruben on 27.12.2018
 */

import com.google.common.base.Preconditions;
import lombok.*;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.FilePacket;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.lib.utility.network.PacketUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Getter
public class PacketOutSendPacket extends Packet {
    private Collection<PacketReceiver> targets;
    private Packet packet;

    public PacketOutSendPacket(Collection<PacketReceiver> targets, Packet packet) {
        super(40);
        Preconditions.checkArgument(
                packet instanceof FilePacket || packet instanceof JsonPacket || packet instanceof SerializationPacket,
                "can only send JsonPacket, SerializationPacket or FilePacket to other nodes"
        );
        this.targets = targets;
        this.packet = packet;
    }

    public PacketOutSendPacket(Packet packet) { //the packet will be sent to all components that are connected to the receiver of this packet
        this(Collections.emptyList(), packet);
    }

    public PacketOutSendPacket(int id) {
        super(id);
    }

    @Override
    public void write(DataOutput dataOutput) throws Exception {
        dataOutput.writeUTF(getClassOfPacket(this.packet).getName());
        this.packet.write(dataOutput);
        dataOutput.writeInt(this.targets.size());
        for (PacketReceiver target : this.targets) {
            target.serialize(dataOutput);
        }
    }

    @Override
    public void read(DataInput dataInput) throws Exception {
        Class<? extends Packet> clazz = (Class<? extends Packet>) Class.forName(dataInput.readUTF());
        this.packet = (Packet) SystemUtils.createConstructorForSerialization(clazz).newInstance();
        this.packet.read(dataInput);
        int length = dataInput.readInt();
        this.targets = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            PacketReceiver receiver = new PacketReceiver();
            receiver.deserialize(dataInput);
            this.targets.add(receiver);
        }
    }

    private Class<? extends Packet> getClassOfPacket(Packet packet) {
        if (packet instanceof FilePacket)
            return FilePacket.class;
        if (packet instanceof JsonPacket)
            return JsonPacket.class;
        if (packet instanceof SerializationPacket)
            return SerializationPacket.class;
        return Packet.class;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static final class PacketReceiver implements PacketSerializable {
        private NetworkComponentType type;
        private String name;

        @Override
        public void serialize(DataOutput dataOutput) throws Exception {
            dataOutput.writeInt(this.type.ordinal());
            dataOutput.writeBoolean(this.name != null);
            if (this.name != null)
                dataOutput.writeUTF(this.name);
        }

        @Override
        public void deserialize(DataInput dataInput) throws Exception {
            this.type = NetworkComponentType.values()[dataInput.readInt()];
            if (dataInput.readBoolean())
                this.name = dataInput.readUTF();
        }
    }
}
