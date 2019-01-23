package net.peepocloud.node;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import jline.console.ConsoleReader;
import net.peepocloud.node.api.libs.InstallableMavenLibrary;
import net.peepocloud.node.libs.DefaultMavenLibraries;
import net.peepocloud.node.libs.LibraryManagerImpl;
import net.peepocloud.node.logging.ColoredLogger;

import java.net.URLClassLoader;

public class PeepoCloudBootstrap {

    public static void main(String[] args) throws Exception {
        ConsoleReader consoleReader = new ConsoleReader(System.in, System.out);
        ColoredLogger logger = new ColoredLogger(consoleReader);

        URLClassLoader systemClassLoader = ClassLoader.getSystemClassLoader() instanceof URLClassLoader ? (URLClassLoader) ClassLoader.getSystemClassLoader() : null;

        LibraryManagerImpl libraryManager = new LibraryManagerImpl(systemClassLoader);

        for (InstallableMavenLibrary defaultLibrary : DefaultMavenLibraries.DEFAULT_LIBRARIES) {
            libraryManager.loadMavenLibraryOrDownload(defaultLibrary);
        }

        new PeepoCloudNode(libraryManager, logger);

    }

}
