package net.peepocloud.node.api.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface Database {

    String getName();

    void insert(String name, SimpleJsonObject jsonObject);

    void delete(String name);

    void update(String name, SimpleJsonObject jsonObject);

    void contains(String name, Consumer<Boolean> consumer);

    CompletableFuture<SimpleJsonObject> get(String name);

    void forEach(Consumer<SimpleJsonObject> consumer);
}
