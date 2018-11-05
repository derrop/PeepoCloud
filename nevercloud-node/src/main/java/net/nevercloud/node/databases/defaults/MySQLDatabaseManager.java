package net.nevercloud.node.databases.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.nevercloud.node.databases.Database;
import net.nevercloud.node.databases.DatabaseConfig;
import net.nevercloud.node.databases.DatabaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MySQLDatabaseManager implements DatabaseManager {
    private Map<String, Database> databases = new HashMap<>();

    private DatabaseConfig config;

    private Connection connection;

    Connection getConnection() {
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
    public Collection<Database> getDatabases() {
        return null;
    }

    @Override
    public void deleteDatabase(String name) {

    }

    @Override
    public void deleteDatabase(Database database) {

    }

    @Override
    public int getDefaultPort() {
        return 3306;
    }

    @Override
    public void connect(DatabaseConfig config) {
        this.config = config;
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabase() + "?autoReconnect=true", config.getUsername(), config.getPassword());
        } catch (SQLException e) {
            System.err.println("Could not connect to mysql database");
            e.printStackTrace();
        }
    }
}
