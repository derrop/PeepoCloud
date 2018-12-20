package net.peepocloud.node.database.defaults.mongo;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.*;
import net.peepocloud.commons.config.json.SimpleJsonObject;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.database.Database;
import org.bson.Document;

import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class MongoDatabase implements Database {
    @Getter
    private String name;
    private MongoCollection<Document> collection;
    @Override
    public void insert(String name, SimpleJsonObject jsonObject) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            collection.insertOne(
                    new Document("name", name).append("value", jsonObject.toString())
            );
        });
    }

    @Override
    public void delete(String name) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            collection.deleteOne(Filters.eq("name", name));
        });
    }

    @Override
    public void update(String name, SimpleJsonObject jsonObject) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            collection.updateOne(Filters.eq("name", name), new Document("$set", new Document("value", jsonObject.toString())));
        });
    }

    @Override
    public void contains(String name, Consumer<Boolean> consumer) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            consumer.accept(collection.find(Filters.eq("name", name)).first() != null);
        });
    }

    @Override
    public void get(String name, Consumer<SimpleJsonObject> consumer) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            Document document = collection.find(Filters.eq("name", name)).first();
            if (document != null) {
                consumer.accept(new SimpleJsonObject(document.getString("value")));
            }
        });
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
