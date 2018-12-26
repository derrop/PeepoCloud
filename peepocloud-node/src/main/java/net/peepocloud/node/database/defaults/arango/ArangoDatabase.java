package net.peepocloud.node.database.defaults.arango;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import com.arangodb.model.AqlQueryOptions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.database.Database;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ArangoDatabase implements Database {
    @Getter
    private String name;
    private ArangoCollection collection;
    @Override
    public void insert(String name, SimpleJsonObject jsonObject) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            BaseDocument document = new BaseDocument();
            document.setKey(name);
            document.addAttribute("val", jsonObject.toJson());
            this.collection.insertDocument(document);
        });
    }

    @Override
    public void delete(String name) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> this.collection.deleteDocument(name));
    }

    @Override
    public void update(String name, SimpleJsonObject jsonObject) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            BaseDocument document = new BaseDocument();
            document.setKey(name);
            document.addAttribute("val", jsonObject.toJson());
            this.collection.updateDocument(name, document);
        });
    }

    @Override
    public void contains(String name, Consumer<Boolean> consumer) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> consumer.accept(this.collection.documentExists(name)));
    }

    @Override
    public CompletableFuture<SimpleJsonObject> get(String name) {
        CompletableFuture<SimpleJsonObject> future = new CompletableFuture<>();
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> {
            future.complete(this.documentToJson(this.collection.getDocument(name, BaseDocument.class)));
        });
        return future;
    }

    @Override
    public void forEach(Consumer<SimpleJsonObject> consumer) {
        ArangoCursor<BaseDocument> cursor = this.collection.db().query(
                "FOR i IN " + this.collection.name() + " RETURN i.val",
                new HashMap<>(),
                new AqlQueryOptions(),
                BaseDocument.class
        );
        while (cursor.hasNext()) {
            SimpleJsonObject jsonObject = this.documentToJson(cursor.next());
            if (jsonObject != null) {
                consumer.accept(jsonObject);
            }
        }
    }

    private SimpleJsonObject documentToJson(BaseDocument document) {
        if (document == null) {
            return null;
        }
        Object json = document.getAttribute("val");
        if (json == null) {
            return null;
        }
        return new SimpleJsonObject(json.toString());
    }
}
