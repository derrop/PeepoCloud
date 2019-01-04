package net.peepocloud.lib.server.bungee;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.lib.AbstractPeepoCloudAPI;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;
import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.minecraft.MinecraftState;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BungeeCordProxyInfo implements PacketSerializable {

    private String componentName;
    private String groupName;
    private int componentId;
    private String parentComponentName;
    private int memory;
    private String host;
    private int port;
    @Setter
    private int pid;

    private Map<UUID, String> players;

    private Template template;

    @Setter
    private long startup;

    public BungeeGroup getGroup() {
        return AbstractPeepoCloudAPI.getInstance().getBungeeGroup(this.groupName);
    }

    public void shutdown() {
        AbstractPeepoCloudAPI.getInstance().stopBungeeProxy(this);
    }

    public void updateFrom(BungeeCordProxyInfo proxyInfo) {
        this.players = proxyInfo.players;
    }

    @Override
    public void serialize(DataOutput dataOutput) throws Exception {
        dataOutput.writeUTF(componentName);
        dataOutput.writeUTF(groupName);
        dataOutput.writeInt(componentId);
        dataOutput.writeUTF(parentComponentName);
        dataOutput.writeInt(memory);
        dataOutput.writeUTF(host);
        dataOutput.writeInt(port);
        dataOutput.writeInt(players.size());
        for (Map.Entry<UUID, String> entry : players.entrySet()) {
            dataOutput.writeLong(entry.getKey().getMostSignificantBits());
            dataOutput.writeLong(entry.getKey().getLeastSignificantBits());
            dataOutput.writeUTF(entry.getValue());
        }
        dataOutput.writeUTF(template.getName());
        dataOutput.writeUTF(template.getStorage());
        dataOutput.writeLong(startup);
    }

    @Override
    public void deserialize(DataInput dataInput) throws Exception {
        componentName = dataInput.readUTF();
        groupName = dataInput.readUTF();
        componentId = dataInput.readInt();
        parentComponentName = dataInput.readUTF();
        memory = dataInput.readInt();
        host = dataInput.readUTF();
        port = dataInput.readInt();
        int size = dataInput.readInt();
        players = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            players.put(new UUID(dataInput.readLong(), dataInput.readLong()), dataInput.readUTF());
        }
        template = new Template(dataInput.readUTF(), dataInput.readUTF());
        startup = dataInput.readLong();
    }
}
