package net.nevercloud.node.databases;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.nevercloud.lib.json.SimpleJsonObject;

import java.util.function.Consumer;

public interface Database {

    String getName();

    void insert(String name, SimpleJsonObject jsonObject);

    void delete(String name);

    void update(String name, SimpleJsonObject jsonObject);

    void get(String name, Consumer<SimpleJsonObject> consumer);

}
