package net.peepocloud.lib.server.bungee;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.peepocloud.lib.AbstractPeepoCloudAPI;
import net.peepocloud.lib.network.packet.serialization.ReflectivePacketSerializable;
import net.peepocloud.lib.server.GroupMode;
import net.peepocloud.lib.server.Template;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BungeeGroup implements ReflectivePacketSerializable {
    private String name;
    private GroupMode groupMode;
    private List<Template> templates;
    private int memory;
    private int minServers;
    private int maxServers;
    private int startPort;
    private boolean maintenance;

    public void update() {
        AbstractPeepoCloudAPI.getInstance().updateBungeeGroup(this);
    }

}
