package net.peepocloud.node.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.peepocloud.commons.config.yaml.YamlConfigurable;
import net.peepocloud.commons.utility.SystemUtils;
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

public class DatabaseLoader {

    private AddonManager<DatabaseAddon> addonManager;

    public DatabaseLoader(String addonsDir) throws IOException {
        this.addonManager = new AddonManager<>();
        this.addonManager.loadAddons(addonsDir, databaseAddon -> databaseAddon.databaseLoader = this);
        this.addonManager.enableAddons();
    }

    public void shutdown() {
        this.addonManager.disableAndUnloadAddons();
    }

    private void connectFailed() {
        System.err.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.connectFailed"));
        SystemUtils.sleepUninterruptedly(5000);
        System.exit(0);
    }

    private boolean doConnect(DatabaseManager databaseManager, DatabaseConfig config) {
        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.connecting")
                .replace("%host%", config.getHost() + ":" + config.getPort()));
        try {
            if (databaseManager.connect(config)) {
                System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.successConnect")
                        .replace("%name%", databaseManager.getClass().getSimpleName())
                        .replace("%host%", config.getHost() + ":" + config.getPort()));
                return true;
            } else {
                System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.failedConnect")
                        .replace("%name%", databaseManager.getClass().getSimpleName())
                        .replace("%host%", config.getHost() + ":" + config.getPort()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    void enableDatabase(DatabaseAddon addon, DatabaseManager databaseManager) {
        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.loading")
                .replace("%name%", databaseManager.getClass().getSimpleName()).replace("%author%", addon.getAddonConfig().getAuthor()));

        DatabaseManager oldDatabaseManager = PeepoCloudNode.getInstance().getDatabaseManager();

        PeepoCloudNode.getInstance().setDatabaseManager(databaseManager);
        DatabaseConfig config = loadConfig(databaseManager);
        if (doConnect(databaseManager, config)) {
            oldDatabaseManager.shutdown();
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.successLoad")
                    .replace("%name%", databaseManager.getClass().getSimpleName()).replace("%author%", addon.getAddonConfig().getAuthor()));
        } else {
            if (oldDatabaseManager == null || !oldDatabaseManager.isConnected()) {
                connectFailed();
            } else {
                System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.connectFailedUsingOld")
                        .replace("%name%", databaseManager.getClass().getSimpleName())
                        .replace("%old%", oldDatabaseManager.getClass().getSimpleName()));
            }
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
                                PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.setup.request").replace("%dbs%", dbs),
                                PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.setup.invalid").replace("%dbs%", dbs),
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

        if (!this.doConnect(databaseManager, config)) {
            connectFailed();
        }

        return databaseManager;
    }

    public DatabaseConfig loadConfig(DatabaseManager databaseManager) {
        DatabaseConfig config;

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

            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.configCreate"));
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
