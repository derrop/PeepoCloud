package net.peepocloud.node.libs;
/*
 * Created by Mc_Ruben on 22.01.2019
 */

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.peepocloud.node.api.languagesystem.LanguagesManager;
import net.peepocloud.node.api.libs.Library;
import net.peepocloud.node.api.libs.LibraryManager;
import net.peepocloud.node.api.libs.MavenLibrary;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LibraryManagerImpl implements LibraryManager {

    private static Method ADD_URL;

    static {
        try {
            ADD_URL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ADD_URL.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Collection<Library> libraries = new ArrayList<>();
    private String directory = "files/libs";
    @Getter
    private URLClassLoader classLoader;
    @Getter
    @Setter
    private LanguagesManager languagesManager;

    public LibraryManagerImpl(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setClassLoader(URLClassLoader classLoader) {
        Preconditions.checkArgument(this.classLoader == null);
        this.classLoader = classLoader;
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    @Override
    public void addURL(URL url) {
        checkEnabled();
        try {
            ADD_URL.invoke(this.classLoader, url);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Library loadLibrary(String name) {
        checkEnabled();
        if (this.isLibraryLoaded(name))
            return null;
        DefaultLibrary library = new DefaultLibrary(this, name);
        if (!library.isInstalled())
            return null;
        this.libraries.add(library);
        library.load();
        return library;
    }

    @Override
    public Library loadLibraryOrDownload(String name, String downloadUrl) {
        checkEnabled();
        if (this.isLibraryLoaded(name))
            return null;
        DefaultLibrary library = new DefaultLibrary(this, name);
        library.installIfNotExists(downloadUrl);
        this.libraries.add(library);
        library.load();
        return library;
    }

    @Override
    public MavenLibrary loadMavenLibraryOrDownload(String groupId, String artifactId, String version, String repo) {
        checkEnabled();
        MavenLibrary library = new DefaultMavenLibrary(this, groupId, artifactId, version, repo);
        if (this.isLibraryLoaded(library.getAbsoluteName()))
            return null;
        if (!library.installIfNotExists())
            return null;
        this.libraries.add(library);
        library.load();
        return library;
    }

    @Override
    public Library getLibrary(String name) {
        checkEnabled();
        return this.libraries.stream().filter(library -> library.getAbsoluteName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public boolean isLibraryLoaded(String name) {
        checkEnabled();
        return this.libraries.stream().anyMatch(library -> library.getAbsoluteName().equals(name) && library.isLoaded());
    }

    @Override
    public boolean unloadLibrary(String name) {
        checkEnabled();
        Library library = this.getLibrary(name);
        if (library == null || !library.isLoaded())
            return false;
        library.unload();
        this.libraries.remove(library);
        return true;
    }

    @Override
    public String getDirectory() {
        checkEnabled();
        return directory;
    }

    @Override
    public void setDirectory(String directory) {
        checkEnabled();
        this.directory = directory.endsWith("/") ? directory.substring(0, directory.length() - 1) : directory;
    }

    @Override
    public Collection<Library> getLibraries() {
        checkEnabled();
        return Collections.unmodifiableCollection(this.libraries);
    }

    private void checkEnabled() {
        if (this.classLoader == null)
            throw new IllegalStateException("LibraryManager not enabled");
    }
    
}
