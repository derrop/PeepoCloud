package net.peepocloud.node.api.libs;
/*
 * Created by Mc_Ruben on 22.01.2019
 */

import java.nio.file.Path;

public interface Library {

    void load();

    void unload();

    ClassLoader getClassLoader();

    boolean isInstalled();

    boolean installIfNotExists(String url);

    boolean forceInstall(String url);

    Path getPath();

    String getAbsoluteName();

    boolean isLoaded();

}
