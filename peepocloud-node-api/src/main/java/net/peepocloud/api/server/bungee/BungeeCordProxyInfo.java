package net.peepocloud.api.server.bungee;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.api.PeepoAPI;
import net.peepocloud.api.server.Template;

import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BungeeCordProxyInfo {

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
        return PeepoAPI.getInstance().getBungeeGroup(this.groupName);
    }

    public void shutdown() {
        PeepoAPI.getInstance().stopBungeeProxy(this);
    }

}
