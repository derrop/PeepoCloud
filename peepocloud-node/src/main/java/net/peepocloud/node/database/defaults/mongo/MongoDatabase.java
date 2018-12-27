package net.peepocloud.node.database.defaults.mongo;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.*;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.database.Database;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class MongoDatabase implements Database {
    @Getter
    private String name;
    private MongoCollection<Document> collection;

    @Override
    public void insert(String name, SimpleJsonObject jsonObject) {
        collection.insertOne(new Document("name", name).append("value", jsonObject.toString()));
    }

    @Override
    public void delete(String name) {
        collection.deleteOne(Filters.eq("name", name));
    }

    @Override
    public void update(String name, SimpleJsonObject jsonObject) {
        collection.updateOne(Filters.eq("name", name), new Document("$set", new Document("value", jsonObject.toString())));
    }

    @Override
    public CompletableFuture<Boolean> contains(String name) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            future.complete(collection.find(Filters.eq("name", name)).first() != null);
        });
        return future;
    }

    @Override
    public CompletableFuture<SimpleJsonObject> get(String name) {
        CompletableFuture<SimpleJsonObject> future = new CompletableFuture<>();
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            Document document = collection.find(Filters.eq("name", name)).first();
            if (document != null) {
                future.complete(new SimpleJsonObject(document.getString("value")));
            } else {
                future.complete(null);
            }
        });
        return future;
    }

    @Override
    public void forEach(Consumer<SimpleJsonObject> consumer) {
        collection.find().forEach((Consumer<? super Document>) document -> {
            if (document != null) {
                consumer.accept(new SimpleJsonObject(document.getString("value")));
            }
        });
    }
}
