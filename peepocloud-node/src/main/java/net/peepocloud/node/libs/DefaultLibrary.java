package net.peepocloud.node.libs;
/*
 * Created by Mc_Ruben on 22.01.2019
 */

import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.lib.utility.ZipUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.libs.Library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultLibrary implements Library {

    public DefaultLibrary(LibraryManagerImpl libraryManager, String name) {
        this.libraryManager = libraryManager;
        this.name = name;
    }

    protected LibraryManagerImpl libraryManager;
    private String name;

    @Override
    public void load() {
        System.out.println(
                "Loading library %name%..."
                        .replace("%name%", getAbsoluteName())
        );
        try {
            this.libraryManager.addURL(this.getPath().getParent().toUri().toURL());
            System.out.println(
                    "&aSuccessfully loaded library %name%"
                            .replace("%name%", getAbsoluteName())
            );
        } catch (IOException e) {
            System.out.println(
                    "&cFailed to load library %name%".replace("%name%", getAbsoluteName())
            );
            e.printStackTrace();
        }
    }

    @Override
    public void unload() {
        throw new UnsupportedOperationException("unload not supported");
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public boolean isInstalled() {
        return Files.exists(this.getPath());
    }

    @Override
    public boolean installIfNotExists(String url) {
        return Files.exists(this.getPath().getParent()) || this.forceInstall(url);
    }

    @Override
    public boolean forceInstall(String url) {
        System.out.println(
                "Installing library %name% from %url%..."
                        .replace("%name%", getAbsoluteName()).replace("%url%", url)
        );
        Path path = this.getPath();
        if (!SystemUtils.downloadFileSynchronized(url, path)) {
            System.out.println(
                    "&cFailed to download library %name% from %url%"
                            .replace("%name%", getAbsoluteName()).replace("%url%", url)
            );
            return false;
        }
        ZipUtils.unzipDirectory(path, path.getParent().toString());
        SystemUtils.deleteDirectory(Paths.get(path.getParent().toString(), "META-INF"));
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(
                "&aSuccessfully downloaded library %name%"
                        .replace("%name%", getAbsoluteName())
        );
        return true;
    }

    @Override
    public Path getPath() {
        return Paths.get(this.libraryManager.getDirectory() + "/" + this.name + ".jar");
    }

    @Override
    public String getAbsoluteName() {
        return this.name;
    }

    @Override
    public boolean isLoaded() {
        return isInstalled();
    }

}
