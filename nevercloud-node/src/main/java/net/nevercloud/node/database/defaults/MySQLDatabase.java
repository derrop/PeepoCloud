package net.nevercloud.node.database.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import lombok.*;
import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.database.Database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class MySQLDatabase implements Database {
    private MySQLDatabaseManager databaseManager;
    @Getter
    private String name;

    MySQLDatabase(MySQLDatabaseManager databaseManager, String name) {
        this.databaseManager = databaseManager;
        this.name = name;
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `" + name + "` (`name` VARCHAR(255), `value` LONGBLOB)");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(String name, SimpleJsonObject jsonObject) {
        NeverCloudNode.getInstance().getExecutorService().execute(() -> {
            try {
                PreparedStatement statement = databaseManager.getConnection().prepareStatement("INSERT INTO `" + this.name + "` (`name`, `value`) VALUES (?, ?)");
                statement.setString(1, name);
                statement.setBytes(2, jsonObject.toBytes());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void delete(String name) {
        NeverCloudNode.getInstance().getExecutorService().execute(() -> {
            try {
                PreparedStatement statement = databaseManager.getConnection().prepareStatement("DELETE FROM `" + this.name + "` WHERE `name` = ?");
                statement.setString(1, name);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void update(String name, SimpleJsonObject jsonObject) {
        NeverCloudNode.getInstance().getExecutorService().execute(() -> {
            try {
                PreparedStatement statement = databaseManager.getConnection().prepareStatement("UPDATE `" + this.name + "` SET `value` = ? WHERE `name` = ?");
                statement.setBytes(1, jsonObject.toBytes());
                statement.setString(2, name);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void get(String name, Consumer<SimpleJsonObject> consumer) {
        NeverCloudNode.getInstance().getExecutorService().execute(() -> {
            try {
                PreparedStatement statement = databaseManager.getConnection().prepareStatement("SELECT `value` FROM `" + this.name + "` WHERE `name` = ?");
                statement.setString(1, name);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    byte[] bytes = resultSet.getBytes("value");
                    if (bytes.length != 0) {
                        try (InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(bytes), "UTF-8")) {
                            consumer.accept(new SimpleJsonObject(reader));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    consumer.accept(null);
                }
                statement.close();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
