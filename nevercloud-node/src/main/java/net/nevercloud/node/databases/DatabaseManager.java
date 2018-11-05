package net.nevercloud.node.databases;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import java.util.Collection;

public interface DatabaseManager {

    Database getDatabase(String name);

    Collection<Database> getDatabases();

    void deleteDatabase(String name);

    void deleteDatabase(Database database);

    int getDefaultPort();

    void connect(DatabaseConfig config);

}
