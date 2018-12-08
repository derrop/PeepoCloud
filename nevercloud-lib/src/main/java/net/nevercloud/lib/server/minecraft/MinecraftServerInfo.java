package net.nevercloud.lib.server.minecraft;
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
public class MinecraftServerInfo {

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

    public MinecraftGroup getGroup() {
        throw new UnsupportedOperationException("Stub!");
    }

}
