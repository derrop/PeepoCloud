package net.peepocloud.lib.player;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import lombok.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.peepocloud.lib.AbstractPeepoCloudAPI;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PeepoPlayer implements PacketSerializable {

    private UUID uniqueId;
    private String name;
    private PlayerConnection playerConnection;
    private String proxyName;
    @Setter
    private String serverName;
    private PeepoClientSettings clientSettings;

    public void sendMessage(BaseComponent... components) {
        AbstractPeepoCloudAPI.getInstance().sendPlayerMessage(this.uniqueId, components);
    }

    public void sendMessage(String message) {
        AbstractPeepoCloudAPI.getInstance().sendPlayerMessage(this.uniqueId, message);
    }

    public void connect(String server) {
        AbstractPeepoCloudAPI.getInstance().sendPlayer(this.uniqueId, server);
    }

    public void connectFallback() {
        AbstractPeepoCloudAPI.getInstance().sendPlayerFallback(this.uniqueId);
    }

    public void kick(String reason) {
        AbstractPeepoCloudAPI.getInstance().kickPlayer(this.uniqueId, reason);
    }

    public void kick(BaseComponent... reason) {
        AbstractPeepoCloudAPI.getInstance().kickPlayer(this.uniqueId, reason);
    }

    public void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        AbstractPeepoCloudAPI.getInstance().sendPlayerTitle(this.uniqueId, title, subTitle, fadeIn, stay, fadeOut);
    }

    public void sendActionBar(String message) {
        AbstractPeepoCloudAPI.getInstance().sendPlayerActionBar(this.uniqueId, message);
    }

    public void sendActionBar(BaseComponent... components) {
        AbstractPeepoCloudAPI.getInstance().sendPlayerMessage(this.uniqueId, components);
    }

    public void chat(String message) {
        AbstractPeepoCloudAPI.getInstance().playerChat(this.uniqueId, message);
    }

    public void setTabHeaderFooter(BaseComponent[] header, BaseComponent[] footer) {
        AbstractPeepoCloudAPI.getInstance().setPlayerTabHeaderFooter(this.uniqueId, header, footer);
    }

    @Override
    public void serialize(DataOutput dataOutput) throws Exception {
        dataOutput.writeLong(uniqueId.getMostSignificantBits());
        dataOutput.writeLong(uniqueId.getLeastSignificantBits());
        dataOutput.writeUTF(name);
        playerConnection.serialize(dataOutput);
        dataOutput.writeUTF(proxyName);
        dataOutput.writeUTF(String.valueOf(proxyName));
        dataOutput.writeBoolean(clientSettings != null);
        if (clientSettings != null) {
            clientSettings.serialize(dataOutput);
        }
    }

    @Override
    public void deserialize(DataInput dataInput) throws Exception {
        uniqueId = new UUID(dataInput.readLong(), dataInput.readLong());
        name = dataInput.readUTF();
        playerConnection = new PlayerConnection();
        playerConnection.deserialize(dataInput);
        proxyName = dataInput.readUTF();
        serverName = dataInput.readUTF();
        if (serverName.equals("null"))
            serverName = null;
        if (dataInput.readBoolean()) {
            clientSettings = new PeepoClientSettings();
            clientSettings.deserialize(dataInput);
        }
    }
}
