package net.nevercloud.lib.server;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BungeeCordProxyInfo {

    private String componentName;
    private int componentId;
    private String parentComponentName;

    private Map<UUID, String> players;

}
