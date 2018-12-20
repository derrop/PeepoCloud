package net.peepocloud.lib.server.minecraft;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.lib.server.Template;

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

    @Setter
    private int maxPlayers;
    @Setter
    private String motd;
    @Setter
    private MinecraftState state;

    private Map<UUID, String> players;

    private Template template;

    @Setter
    private long startup;


}
