package net.nevercloud.node.database.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.database.Database;
import net.nevercloud.node.database.DatabaseConfig;
import net.nevercloud.node.database.DatabaseManager;

import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MySQLDatabaseManager implements DatabaseManager {
    private Map<String, Database> databases = new HashMap<>();

    private boolean connectionClosed = false;
    private DatabaseConfig config;

    private Connection connection;

    Connection getConnection() {
        if (connectionClosed)
            return connection;
        try {
            if (connection == null || connection.isClosed()) {
                this.connect(this.config);
                Thread.sleep(400);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public Database getDatabase(String name) {
        if (databases.containsKey(name))
            return databases.get(name);
        Database database = new MySQLDatabase(this, name);
        databases.put(name, database);
        return database;
    }

    @Override
    public void getDatabases(Consumer<Collection<String>> consumer) {
        NeverCloudNode.getInstance().getExecutorService().execute(() -> {
            consumer.accept(Collections.emptyList());//TODO
        });
    }

    @Override
    public void deleteDatabase(String name) {
        NeverCloudNode.getInstance().getExecutorService().execute(() -> {
            try {
                PreparedStatement statement = getConnection().prepareStatement("DROP TABLE `" + name + "`");
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void deleteDatabase(Database database) {
        this.deleteDatabase(database.getName());
    }

    @Override
    public int getDefaultPort() {
        return 3306;
    }

    @Override
    public void connect(DatabaseConfig config) {
        if (connectionClosed)
            return;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.config = config;
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" +
                            config.getHost() + ":" +
                            config.getPort() + "/" +
                            config.getDatabase() +
                            "?autoReconnect=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                    config.getUsername(),
                    config.getPassword());
            System.out.println("&aSuccessfully connected to mysql database &7@" + config.getHost() + ":" + config.getPort());
        } catch (SQLException e) {
            System.err.println("Could not connect to mysql database");
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        connectionClosed = true;
        if (this.connection == null)
            return;
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
