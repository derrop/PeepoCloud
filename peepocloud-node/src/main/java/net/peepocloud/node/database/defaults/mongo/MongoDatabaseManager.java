package net.peepocloud.node.database.defaults.mongo;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.database.Database;
import net.peepocloud.node.database.DatabaseConfig;
import net.peepocloud.node.database.DatabaseManager;

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
        Database database = new net.peepocloud.node.database.defaults.mongo.MongoDatabase(name, this.mongoDatabase.getCollection(name));
        databases.put(name, database);
        return database;
    }

    @Override
    public void getDatabases(Consumer<Collection<String>> consumer) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            Collection<String> strings = new ArrayList<>();
            this.mongoDatabase.listCollectionNames().forEach((Block<? super String>) strings::add);
            consumer.accept(strings);
        });
    }

    @Override
    public void deleteDatabase(String name) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> this.mongoDatabase.getCollection(name).drop());
    }

    @Override
    public int getDefaultPort() {
        return 27017;
    }

    @Override
    public String getName() {
        return "MongoDB";
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean connect(DatabaseConfig config) throws Exception {
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
        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.mongodb.successfullyConnected").replace("%host%", config.getHost() + ":" + config.getPort()));
        this.mongoDatabase = this.mongoClient.getDatabase(config.getDatabase());
        return true;
    }

    @Override
    public void shutdown() {
        this.mongoClient.close();
    }
}
