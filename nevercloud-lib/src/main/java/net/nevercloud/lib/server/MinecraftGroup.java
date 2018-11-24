package net.nevercloud.lib.server;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import lombok.*;

import java.util.Collection;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MinecraftGroup {
    private String name;
    private GroupMode groupMode;
    private Collection<Template> templates;
    private int memory;
    private int minServers;
    private int maxServers;
}
