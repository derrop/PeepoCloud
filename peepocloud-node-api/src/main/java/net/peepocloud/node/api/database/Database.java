package net.peepocloud.node.api.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.node.api.PeepoCloudNodeAPI;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public interface Database {

    String getName();

    void insert(String name, SimpleJsonObject jsonObject);

    void delete(String name);

    void update(String name, SimpleJsonObject jsonObject);

    CompletableFuture<Boolean> contains(String name);

    CompletableFuture<SimpleJsonObject> get(String name);

    void forEach(Consumer<SimpleJsonObject> consumer);

    default void insertAsync(String name, SimpleJsonObject jsonObject) {
        PeepoCloudNodeAPI.getInstance().getExecutorService().execute(() -> {
            this.insert(name, jsonObject);
        });
    }

    default void updateAsync(String name, SimpleJsonObject jsonObject) {
        PeepoCloudNodeAPI.getInstance().getExecutorService().execute(() -> {
            this.update(name, jsonObject);
        });
    }

    default void deleteAsync(String name) {
        PeepoCloudNodeAPI.getInstance().getExecutorService().execute(() -> {
            this.delete(name);
        });
    }

    default boolean containsSync(String name) {
        try {
            return contains(name).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    default void forEachAsync(Consumer<SimpleJsonObject> consumer) {
        PeepoCloudNodeAPI.getInstance().getExecutorService().execute(() -> {
            this.forEach(consumer);
        });
    }

}
