package net.peepocloud.node.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.peepocloud.lib.config.yaml.YamlConfigurable;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.addon.AddonManager;
import net.peepocloud.node.database.defaults.arango.ArangoDatabaseManager;
import net.peepocloud.node.database.defaults.mongo.MongoDatabaseManager;
import net.peepocloud.node.database.defaults.mysql.MySQLDatabaseManager;
import net.peepocloud.node.setup.type.ArraySetupAcceptable;
import net.peepocloud.node.setup.Setup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DatabaseLoader { //TODO implement languagesystem

    private AddonManager<DatabaseAddon> addonManager;

    public DatabaseLoader(String addonsDir) throws IOException {
        this.addonManager = new AddonManager<>();
        this.addonManager.loadAddons(addonsDir, databaseAddon -> databaseAddon.databaseLoader = this);
        this.addonManager.enableAddons();
    }

    public void shutdown() {
        this.addonManager.disableAndUnloadAddons();
    }

    void enableDatabase(DatabaseAddon addon, DatabaseManager databaseManager) {
        System.out.println("&eLoading database &9" + databaseManager.getClass().getSimpleName() + " &eby &6" + addon.getAddonConfig().getAuthor() + "&e...");

        if (PeepoCloudNode.getInstance().getDatabaseManager() != null) {
            PeepoCloudNode.getInstance().getDatabaseManager().shutdown();
        }

        PeepoCloudNode.getInstance().setDatabaseManager(databaseManager);
        DatabaseConfig config = loadConfig(databaseManager);
        System.out.println("&eTrying to connect to database &7@" + config.getHost() + ":" + config.getPort() + "&e...");
        try {
            databaseManager.connect(config);
            System.out.println("&aSuccessfully loaded database &9" + databaseManager.getClass().getSimpleName() + " &eby &6" + addon.getAddonConfig().getAuthor());
            System.out.println("&aSuccessfully connected to database &7@" + config.getHost() + ":" + config.getPort() + " &c(MAKE SURE THAT ALL NODES ARE CONNECTED TO THE SAME DATABASE)");
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

    public DatabaseManager loadDatabaseManager(PeepoCloudNode node) {
        Map<String, DatabaseManager> databases = new HashMap<>();
        this.addonManager.getLoadedAddons().values().forEach(databaseAddon -> {
            DatabaseManager databaseManager = databaseAddon.loadDatabaseManager();
            if (databaseManager != null) {
                databases.put(databaseManager.getName(), databaseManager);
            }
        });
        Arrays.asList(new MySQLDatabaseManager(), new ArangoDatabaseManager(), new MongoDatabaseManager())
                .forEach(databaseManager -> databases.put(databaseManager.getName(), databaseManager));

        DatabaseManager databaseManager = null;

        if (node.getInternalConfig().contains("databaseManager")) {
            for (DatabaseManager value : databases.values()) {
                if (value.getName().equalsIgnoreCase(node.getInternalConfig().getString("databaseManager"))) {
                    databaseManager = value;
                    break;
                }
            }
        }

        if (databaseManager == null) {
            Setup.startSetupSync(new YamlConfigurable(), PeepoCloudNode.getInstance().getLogger(),
                    setup -> {
                        StringBuilder dbsBuilder = new StringBuilder();
                        for (String s : databases.keySet()) {
                            dbsBuilder.append(s).append(", ");
                        }
                        String dbs = dbsBuilder.substring(0, dbsBuilder.length() - 2);
                        setup.request(
                                "db",
                                "Please specify the database that you want to use: " + dbs,
                                "Invalid input, please choose one of the following and if you want more databases than those, make suggestions on our discord or write your own: " + dbs,
                                new ArraySetupAcceptable<>(databases.keySet().toArray())
                        );
                        node.getInternalConfig().append("databaseManager", setup.getData().getString("db").toLowerCase());
                        node.saveInternalConfigFile();
                    });
            for (DatabaseManager value : databases.values()) {
                if (value.getName().equalsIgnoreCase(node.getInternalConfig().getString("databaseManager"))) {
                    databaseManager = value;
                    break;
                }
            }
        }

        DatabaseConfig config = loadConfig(databaseManager);

        System.out.println("&eTrying to connect to database &7@" + config.getHost() + ":" + config.getPort() + "&e...");
        try {
            databaseManager.connect(config);
            System.out.println("&aSuccessfully connected to database &7@" + config.getHost() + ":" + config.getPort() + " &c(MAKE SURE THAT ALL NODES ARE CONNECTED TO THE SAME DATABASE)");
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
            YamlConfigurable configurable = YamlConfigurable.load(path);
            config = new DatabaseConfig(
                    configurable.getString("host"),
                    configurable.getInt("port"),
                    configurable.getString("username"),
                    configurable.getString("password"),
                    configurable.getString("database")
            );
        } else {
            config = new DatabaseConfig(
                    "127.0.0.1",
                    databaseManager.getDefaultPort(),
                    "root",
                    "your_password",
                    "peepocloud"
            );

            new YamlConfigurable()
                    .append("host", config.getHost())
                    .append("port", config.getPort())
                    .append("username", config.getUsername())
                    .append("password", config.getPassword())
                    .append("database", config.getDatabase())
                    .saveAsFile(path);

            System.out.println("&4Your database configuration file was created, please configure it and restart the System");
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
