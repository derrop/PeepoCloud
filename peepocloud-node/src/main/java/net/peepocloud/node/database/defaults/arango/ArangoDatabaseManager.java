package net.peepocloud.node.database.defaults.arango;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.CollectionEntity;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.database.Database;
import net.peepocloud.node.api.database.DatabaseConfig;
import net.peepocloud.node.api.database.DatabaseManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ArangoDatabaseManager implements DatabaseManager {
    private ArangoDB arangoDB;
    private ArangoDatabase arangoDatabase;
    private Map<String, Database> databases = new HashMap<>();

    @Override
    public Database getDatabase(String name) {
        if (this.databases.containsKey(name))
            return this.databases.get(name);
        ArangoCollection collection = this.arangoDatabase.collection(name);
        if (!collection.exists()) {
            this.arangoDatabase.createCollection(name);
        }
        Database database = new net.peepocloud.node.database.defaults.arango.ArangoDatabase(name, collection);
        this.databases.put(name, database);
        return database;
    }

    @Override
    public void getDatabases(Consumer<Collection<String>> consumer) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            consumer.accept(this.arangoDatabase.getCollections().stream().map(CollectionEntity::getName).collect(Collectors.toList()));
        });
    }

    @Override
    public void deleteDatabase(String name) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            ArangoCollection collection = this.arangoDatabase.collection(name);
            if (collection != null) {
                collection.drop();
            }
        });
    }

    @Override
    public int getDefaultPort() {
        return 8529;
    }

    @Override
    public String getName() {
        return "ArangoDB";
    }

    @Override
    public boolean isConnected() {
        return this.arangoDatabase.exists();
    }

    @Override
    public boolean connect(DatabaseConfig config) {
        this.arangoDB = new ArangoDB.Builder()
                .host(config.getHost(), config.getPort())
                .user(config.getUsername())
                .password(config.getPassword())
                .build();
        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.arangodb.successfullyConnected").replace("%host%", config.getHost() + ":" + config.getPort()));

        this.arangoDatabase = this.arangoDB.db(config.getDatabase());

        if(!this.arangoDatabase.exists())
            this.arangoDB.createDatabase(config.getDatabase());

        return this.isConnected();
    }

    @Override
    public void shutdown() {
        this.arangoDB.shutdown();
    }
}
