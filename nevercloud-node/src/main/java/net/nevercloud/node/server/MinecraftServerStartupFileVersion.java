package net.nevercloud.node.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;

@Getter
@AllArgsConstructor
public enum MinecraftServerStartupFileVersion {
    SPIGOT("", new ServerVersion[]{new ServerVersion("1.12.2", "https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar")}),
    PAPER("", new ServerVersion[]{new ServerVersion("1.12.2", "")}),
    TACO("", new ServerVersion[]{new ServerVersion("1.12.2", "")});

    private String url;
    private ServerVersion[] versions;

    public ServerVersion getVersion(String name) {
        for (ServerVersion version : this.versions) {
            if (version.getName().equalsIgnoreCase(name)) {
                return version;
            }
        }
        return null;
    }

    public static String asString() {
        return "spigot, paper, taco";
    }
}
