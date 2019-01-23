package net.peepocloud.node.libs;
/*
 * Created by Mc_Ruben on 22.01.2019
 */

import net.peepocloud.node.api.libs.LibraryManager;
import net.peepocloud.node.api.libs.MavenLibrary;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultMavenLibrary extends DefaultLibrary implements MavenLibrary {

    private String groupId, artifactId, version, repo;

    public DefaultMavenLibrary(LibraryManagerImpl libraryManager, String groupId, String artifactId, String version, String repo) {
        super(libraryManager, groupId + ":" + artifactId + ":" + version);
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.repo = repo.endsWith("/") ? repo : repo + "/";
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getRepo() {
        return this.repo;
    }

    @Override
    public boolean forceInstall() {
        return super.forceInstall(this.repo + path());
    }

    @Override
    public boolean installIfNotExists() {
        return super.installIfNotExists(this.repo + path());
    }

    @Override
    public Path getPath() {
        return Paths.get(this.libraryManager.getDirectory() + "/" + path());
    }

    private String path() {
        return this.getGroupId().replace('.', '/') + "/" + this.getArtifactId() + "/" + this.getVersion() + "/" + this.getArtifactId() + "-" + this.getVersion() + ".jar";
    }
}
