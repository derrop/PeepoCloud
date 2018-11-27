package net.nevercloud.node.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import java.util.Collection;
import java.util.function.Consumer;

public interface DatabaseManager {

    Database getDatabase(String name);

    void getDatabases(Consumer<Collection<String>> consumer);

    void deleteDatabase(String name);

    void deleteDatabase(Database database);

    int getDefaultPort();

    String getName();

    boolean isConnected();

    void connect(DatabaseConfig config) throws Exception;

    void shutdown();

}
