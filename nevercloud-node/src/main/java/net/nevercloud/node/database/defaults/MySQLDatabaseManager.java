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
                try {
                    this.connect(this.config);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void connect(DatabaseConfig config) throws Exception {
        if (connectionClosed)
            return;

        Class.forName("com.mysql.cj.jdbc.Driver");

        this.config = config;
        this.connection = DriverManager.getConnection(
                "jdbc:mysql://" +
                        config.getHost() + ":" +
                        config.getPort() + "/" +
                        config.getDatabase() +
                        "?autoReconnect=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                config.getUsername(),
                config.getPassword());
        System.out.println(NeverCloudNode.getInstance().getLanguagesManager().getMessage("database.mysqldb.successfullyConnected").replace("%host%", config.getHost() + ":" + config.getPort()));
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
