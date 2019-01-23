package net.peepocloud.node.libs;
/*
 * Created by Mc_Ruben on 22.01.2019
 */

import net.peepocloud.node.api.libs.DefaultMavenRepositories;
import net.peepocloud.node.api.libs.InstallableMavenLibrary;

import java.util.Arrays;
import java.util.Collection;

public class DefaultMavenLibraries {

    public static final Collection<InstallableMavenLibrary> DEFAULT_LIBRARIES = Arrays.asList(
            //others
            new InstallableMavenLibrary("com.google.code.gson", "gson", "2.8.5", DefaultMavenRepositories.CENTRAL),
            new InstallableMavenLibrary("io.netty", "netty-all", "4.1.30.Final", DefaultMavenRepositories.CENTRAL),
            new InstallableMavenLibrary("jline", "jline", "2.14.6", DefaultMavenRepositories.CENTRAL),

            //databases
            new InstallableMavenLibrary("mysql", "mysql-connector-java", "8.0.11", DefaultMavenRepositories.CENTRAL),
            new InstallableMavenLibrary("org.mongodb", "mongodb-driver", "3.9.1", DefaultMavenRepositories.CENTRAL),
            new InstallableMavenLibrary("com.arangodb", "arangodb-java-driver", "4.3.0", DefaultMavenRepositories.CENTRAL)
    );

}
