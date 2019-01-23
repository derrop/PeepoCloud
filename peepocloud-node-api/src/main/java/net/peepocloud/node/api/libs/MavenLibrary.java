package net.peepocloud.node.api.libs;
/*
 * Created by Mc_Ruben on 22.01.2019
 */

public interface MavenLibrary extends Library {

    String getGroupId();

    String getArtifactId();

    String getVersion();

    String getRepo();

    boolean forceInstall();

    boolean installIfNotExists();

    void load();

}
