package net.nevercloud.node.database.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import net.nevercloud.node.database.Database;
import net.nevercloud.node.database.DatabaseConfig;
import net.nevercloud.node.database.DatabaseManager;

import java.util.Collection;
import java.util.function.Consumer;

public class ArangoDatabaseManager implements DatabaseManager {
    private ArangoDB arangoDB;
    private ArangoDatabase arangoDatabase;

    @Override
    public Database getDatabase(String name) {
        return null;
    }

    @Override
    public void getDatabases(Consumer<Collection<String>> consumer) {

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
        this.arangoDB = new ArangoDB.Builder()
                .host(config.getHost(), config.getPort())
                .user(config.getUsername())
                .password(config.getPassword())
                .build();
        System.out.println("&aSuccessfully connected to arangodb &7@" + config.getHost() + ":" + config.getPort());

        this.arangoDatabase = this.arangoDB.db(config.getDatabase());
    }

    @Override
    public void shutdown() {
        this.arangoDB.shutdown();
    }
}
