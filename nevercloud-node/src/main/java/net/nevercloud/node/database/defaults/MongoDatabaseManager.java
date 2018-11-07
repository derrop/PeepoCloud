package net.nevercloud.node.database.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.database.Database;
import net.nevercloud.node.database.DatabaseConfig;
import net.nevercloud.node.database.DatabaseManager;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MongoDatabaseManager implements DatabaseManager {

    private Map<String, Database> databases = new HashMap<>();

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;

    @Override
    public Database getDatabase(String name) {
        if (databases.containsKey(name))
            return databases.get(name);
        this.mongoDatabase.createCollection(name);
        Database database = new net.nevercloud.node.database.defaults.MongoDatabase(name, this.mongoDatabase.getCollection(name));
        databases.put(name, database);
        return database;
    }

    @Override
    public void getDatabases(Consumer<Collection<String>> consumer) {
        NeverCloudNode.getInstance().getExecutorService().execute(() -> {
            Collection<String> strings = new ArrayList<>();
            this.mongoDatabase.listCollectionNames().forEach((Block<? super String>) strings::add);
            consumer.accept(strings);
        });
    }

    @Override
    public void deleteDatabase(String name) {
        NeverCloudNode.getInstance().getExecutorService().execute(() -> this.mongoDatabase.getCollection(name).drop());
    }

    @Override
    public void deleteDatabase(Database database) {
        this.deleteDatabase(database.getName());
    }

    @Override
    public int getDefaultPort() {
        return 27017;
    }

    @Override
    public void connect(DatabaseConfig config) {
        this.mongoClient = MongoClients.create(
                new ConnectionString(
                        MessageFormat.format(
                                "mongodb://{0}:{1}@{2}:{3}/{4}",

                                config.getUsername(),
                                URLEncoder.encode(config.getPassword()),
                                config.getHost(),
                                config.getPort(),
                                config.getDatabase()
                        )
                )
        );
        System.out.println("&aSuccessfully connected to mongodb &7@" + config.getHost() + ":" + config.getPort());
        this.mongoDatabase = this.mongoClient.getDatabase(config.getDatabase());
    }

    @Override
    public void shutdown() {
        this.mongoClient.close();
    }
}
