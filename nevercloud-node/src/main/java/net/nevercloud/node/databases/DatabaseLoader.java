package net.nevercloud.node.databases;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.addons.AddonManager;
import net.nevercloud.node.databases.defaults.ArangoDatabaseManager;
import net.nevercloud.node.databases.defaults.MongoDatabaseManager;
import net.nevercloud.node.databases.defaults.MySQLDatabaseManager;

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

        NeverCloudNode.getInstance().setDatabaseManager(databaseManager);
        databaseManager.connect(loadConfig(databaseManager));
        System.out.println("&aSuccessfully loaded database &9" + databaseManager.getClass().getSimpleName() + " &eby &6" + addon.getAddonConfig().getAuthor());
    }

    public DatabaseManager loadDatabaseManager(NeverCloudNode node) {
        if (!node.getInternalConfig().contains("databaseManager")) {
            System.out.println("Please specify the database type you want to use: MYSQL, ARANGODB, MONGODB");
            String line = node.getLogger().readLineUntil(s -> s.equalsIgnoreCase("mysql") ||
                            s.equalsIgnoreCase("arangodb") ||
                            s.equalsIgnoreCase("mongodb"),
                    "Invalid input, please specify one of the following and if you want more databases than those, make suggestions or write your own: MYSQL, ARANGODB, MONGODB");
            node.getInternalConfig().append("databaseManager", line.toLowerCase());
            node.saveInternalConfigFile();
        }

        DatabaseManager databaseManager = null;

        switch (node.getInternalConfig().getString("databaseManager").toLowerCase()) {
            case "mysql": {
                databaseManager = new MySQLDatabaseManager();
            }

            case "arangodb": {
                databaseManager = new ArangoDatabaseManager();
            }

            case "mongodb": {
                databaseManager = new MongoDatabaseManager();
            }
        }

        if (databaseManager == null) {
            databaseManager = new MySQLDatabaseManager();
        }

        DatabaseConfig config = loadConfig(databaseManager);

        databaseManager.connect(config);

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
            System.out.println("Your database configuration file was created, please configure it and restart the Cloud");
        }

        return config;
    }

}
