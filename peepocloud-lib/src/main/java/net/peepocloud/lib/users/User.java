package net.peepocloud.lib.users;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import lombok.*;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;
import net.peepocloud.lib.utility.SystemUtils;

import java.io.DataInput;
import java.io.DataOutput;

@Data
@ToString
@EqualsAndHashCode
public class User implements PacketSerializable {

    public User(String username, String password, String apiToken) {
        this.username = username;
        this.hashedPassword = SystemUtils.hashString(password);
        this.apiToken = apiToken;
        this.metaData = new SimpleJsonObject();
    }

    private String username;
    private String hashedPassword;
    private String apiToken;
    private SimpleJsonObject metaData;

    @Override
    public void serialize(DataOutput dataOutput) throws Exception {
        dataOutput.writeUTF(username);
        dataOutput.writeUTF(hashedPassword);
        dataOutput.writeUTF(apiToken);
        dataOutput.writeUTF(metaData.toJson());
    }

    @Override
    public void deserialize(DataInput dataInput) throws Exception {
        username = dataInput.readUTF();
        hashedPassword = dataInput.readUTF();
        apiToken = dataInput.readUTF();
        metaData = new SimpleJsonObject(dataInput.readUTF());
    }
}
