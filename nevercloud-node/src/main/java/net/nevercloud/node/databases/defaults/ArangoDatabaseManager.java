package net.nevercloud.node.databases.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.nevercloud.node.databases.Database;
import net.nevercloud.node.databases.DatabaseConfig;
import net.nevercloud.node.databases.DatabaseManager;

import java.util.Collection;

public class ArangoDatabaseManager implements DatabaseManager {
    @Override
    public Database getDatabase(String name) {
        return null;
    }

    @Override
    public Collection<Database> getDatabases() {
        return null;
    }

    @Override
    public void deleteDatabase(String name) {

    }

    @Override
    public void deleteDatabase(Database database) {

    }

    @Override
    public int getDefaultPort() {
        return 8529;
    }

    @Override
    public void connect(DatabaseConfig config) {

    }
}
