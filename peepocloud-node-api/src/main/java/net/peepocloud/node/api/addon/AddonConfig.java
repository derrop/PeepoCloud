package net.peepocloud.node.api.addon;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.peepocloud.node.api.libs.InstallableMavenLibrary;

import java.util.Collection;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AddonConfig {
    private String name;
    private String version;
    private String author;
    private String main;
    private String fileName;
    private String website;
    private ReloadType reloadType;
    private Collection<InstallableMavenLibrary> libraries;

    public static enum ReloadType {
        NEVER, ALWAYS
    }
}
