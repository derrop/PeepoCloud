package net.peepocloud.node.api.libs;
/*
 * Created by Mc_Ruben on 22.01.2019
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.lib.network.packet.serialization.PacketSerializable;

import java.io.DataInput;
import java.io.DataOutput;

@Getter
@AllArgsConstructor
public class InstallableMavenLibrary implements PacketSerializable {

    private String groupId, artifactId, version, repo;

    public String getFullName() {
        return repo + " " + groupId + ":" + artifactId + ":" + version;
    }

    @Override
    public void serialize(DataOutput dataOutput) throws Exception {
        dataOutput.writeUTF(groupId);
        dataOutput.writeUTF(artifactId);
        dataOutput.writeUTF(version);
        dataOutput.writeUTF(repo);
    }

    @Override
    public void deserialize(DataInput dataInput) throws Exception {
        groupId = dataInput.readUTF();
        artifactId = dataInput.readUTF();
        version = dataInput.readUTF();
        repo = dataInput.readUTF();
    }

}
