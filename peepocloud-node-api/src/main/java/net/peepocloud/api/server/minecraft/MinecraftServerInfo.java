package net.peepocloud.api.server.minecraft;
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

    public MinecraftGroup getGroup() {
        return PeepoAPI.getInstance().getMinecraftGroup(this.groupName);
    }

    public void stop() {
        PeepoAPI.getInstance().stopMinecraftServer(this);
    }

}
