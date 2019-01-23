package net.peepocloud.node.api.libs;
/*
 * Created by Mc_Ruben on 22.01.2019
 */

import java.net.URL;
import java.util.Collection;

public interface LibraryManager {

    ClassLoader getClassLoader();

    void addURL(URL url);

    Library loadLibrary(String name);

    Library loadLibraryOrDownload(String name, String downloadUrl);

    MavenLibrary loadMavenLibraryOrDownload(String groupId, String artifactId, String version, String repo);

    default MavenLibrary loadMavenLibraryOrDownload(InstallableMavenLibrary library) {
        return this.loadMavenLibraryOrDownload(library.getGroupId(), library.getArtifactId(), library.getVersion(), library.getRepo());
    }

    Library getLibrary(String name);

    boolean isLibraryLoaded(String name);

    boolean unloadLibrary(String name);

    String getDirectory();

    void setDirectory(String directory);

    Collection<Library> getLibraries();

}
