package net.peepocloud.lib.server.bungee;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.lib.AbstractPeepoCloudAPI;
import net.peepocloud.lib.network.packet.serialization.ReflectivePacketSerializable;
import net.peepocloud.lib.server.Template;

import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BungeeCordProxyInfo implements ReflectivePacketSerializable {

    private String componentName;
    private String groupName;
    private int componentId;
    private String parentComponentName;
    private int memory;
    private String host;
    private int port;

    private Map<UUID, String> players;

    private Template template;

    @Setter
    private long startup;

    public BungeeGroup getGroup() {
        return AbstractPeepoCloudAPI.getInstance().getBungeeGroup(this.groupName);
    }

}
