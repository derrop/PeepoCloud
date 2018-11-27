package net.nevercloud.lib.server.bungee;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.Template;
import net.nevercloud.lib.utility.SystemUtils;

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

    private Map<UUID, String> players;

    private Template template;

    @Setter
    private long startup;

    public BungeeGroup getGroup() {
        throw new UnsupportedOperationException("Stub!");
    }

}
