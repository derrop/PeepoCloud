package net.nevercloud.node.database.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import lombok.*;
import net.nevercloud.lib.conf.json.SimpleJsonObject;
import net.nevercloud.node.database.Database;

import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ArangoDatabase implements Database {
    @Getter
    private String name;
    @Override
    public void insert(String name, SimpleJsonObject jsonObject) {

    }

    @Override
    public void delete(String name) {

    }

    @Override
    public void update(String name, SimpleJsonObject jsonObject) {

    }

    @Override
    public void get(String name, Consumer<SimpleJsonObject> consumer) {

    }
}
