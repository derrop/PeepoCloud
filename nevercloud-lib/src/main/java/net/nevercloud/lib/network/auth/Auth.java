package net.nevercloud.lib.network.auth;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.nevercloud.lib.config.json.SimpleJsonObject;

@Getter
@AllArgsConstructor
public class Auth {
    private String authKey;
    private String componentName;
    private NetworkComponentType type;
    private String parentComponentName;
    private SimpleJsonObject extraData;
}
