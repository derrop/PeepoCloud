package net.peepocloud.node.api.addon.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import lombok.*;

@Data
@AllArgsConstructor
@ToString
public class DefaultAddonConfig {
    private String name;
    private String[] authors;
    private String version;
    private String[] allVersions;
}
