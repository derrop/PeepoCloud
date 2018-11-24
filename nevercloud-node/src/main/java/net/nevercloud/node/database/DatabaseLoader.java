package net.nevercloud.node.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.addon.AddonManager;
import net.nevercloud.node.database.defaults.ArangoDatabaseManager;
import net.nevercloud.node.database.defaults.MongoDatabaseManager;
import net.nevercloud.node.database.defaults.MySQLDatabaseManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DatabaseLoader {

    private AddonManager<DatabaseAddon> addonManager;

    public DatabaseLoader(String addonsDir) throws IOException {
        this.addonManager = new AddonManager<>();
        this.addonManager.loadAddons(addonsDir);
        this.addonManager.enableAddons();
    }

    public void shutdown() {
        this.addonManager.disableAndUnloadAddons();
    }

    void enableDatabase(DatabaseAddon addon, DatabaseManager databaseManager) {
        System.out.println("&eLoading database &9" + databaseManager.getClass().getSimpleName() + " &eby &6" + addon.getAddonConfig().getAuthor() + "&e...");

        if (NeverCloudNode.getInstance().getDatabaseManager() != null) {
            NeverCloudNode.getInstance().getDatabaseManager().shutdown();
        }

        NeverCloudNode.getInstance().setDatabaseManager(databaseManager);
        DatabaseConfig config = loadConfig(databaseManager);
        System.out.println("&eTrying to connect to database &7@" + config.getHost() + ":" + config.getPort() + "&e...");
        try {
            databaseManager.connect(config);
            System.out.println("&aSuccessfully loaded database &9" + databaseManager.getClass().getSimpleName() + " &eby &6" + addon.getAddonConfig().getAuthor());
            System.out.println("&aSuccessfully connected to database &7@" + config.getHost() + ":" + config.getPort()+" &c(MAKE SURE THAT ALL NODES ARE CONNECTED TO THE SAME DATABASE)");
        } catch (Exception e) {
            System.err.println("&cCould not connect to database, system will exit in 5 seconds...");
            e.printStackTrace();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.exit(0);
        }
    }

    public DatabaseManager loadDatabaseManager(NeverCloudNode node) {
        if (!node.getInternalConfig().contains("databaseManager")) {
            System.out.println("Please specify the database that you want to use: MYSQL, ARANGODB, MONGODB");
            String line = node.getLogger().readLineUntil(s -> s.equalsIgnoreCase("mysql") ||
                            s.equalsIgnoreCase("arangodb") ||
                            s.equalsIgnoreCase("mongodb"),
                    "Invalid input, please choose one of the following and if you want more databases than those, make suggestions or write your own: MYSQL, ARANGODB, MONGODB");
            node.getInternalConfig().append("databaseManager", line.toLowerCase());
            node.saveInternalConfigFile();
        }

        DatabaseManager databaseManager = null;

        switch (node.getInternalConfig().getString("databaseManager").toLowerCase()) {
            case "mysql": {
                databaseManager = new MySQLDatabaseManager();
            }
            break;

            case "arangodb": {
                databaseManager = new ArangoDatabaseManager();
            }
            break;

            case "mongodb": {
                databaseManager = new MongoDatabaseManager();
            }
            break;
        }

        if (databaseManager == null) {
            databaseManager = new MySQLDatabaseManager();
        }

        DatabaseConfig config = loadConfig(databaseManager);

        System.out.println("&eTrying to connect to database &7@" + config.getHost() + ":" + config.getPort() + "&e...");
        try {
            databaseManager.connect(config);
            System.out.println("&aSuccessfully connected to database &7@" + config.getHost() + ":" + config.getPort());
        } catch (Exception e) {
            System.err.println("&cCould not connect to database, system will exit in 5 seconds...");
            e.printStackTrace();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.exit(0);
        }

        return databaseManager;
    }

    public DatabaseConfig loadConfig(DatabaseManager databaseManager) {
        DatabaseConfig config = null;

        Path path = Paths.get("database.yml");
        if (Files.exists(path)) {
            try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path), "UTF-8")) {
                Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(reader);
                config = new DatabaseConfig(
                        configuration.getString("host"),
                        configuration.getInt("port"),
                        configuration.getString("username"),
                        configuration.getString("password"),
                        configuration.getString("database")
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            config = new DatabaseConfig(
                    "127.0.0.1",
                    databaseManager.getDefaultPort(),
                    "root",
                    "your_password",
                    "nevercloud"
            );

            Configuration configuration = new Configuration();
            configuration.set("host", config.getHost());
            configuration.set("port", config.getPort());
            configuration.set("username", config.getUsername());
            configuration.set("password", config.getPassword());
            configuration.set("database", config.getDatabase());

            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(path, StandardOpenOption.CREATE_NEW), "UTF-8")) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("&4Your database configuration file was created, please configure it and restart the Cloud");
            System.out.println("&aThe System will exit in 5 seconds...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }

        return config;
    }

}
