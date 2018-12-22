package net.peepocloud.node.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import javafx.util.Pair;
import net.peepocloud.lib.config.yaml.YamlConfigurable;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.addon.AddonManagerImpl;
import net.peepocloud.node.api.addon.AddonManager;
import net.peepocloud.node.api.database.DatabaseAddon;
import net.peepocloud.node.api.database.DatabaseConfig;
import net.peepocloud.node.api.database.DatabaseLoader;
import net.peepocloud.node.api.database.DatabaseManager;
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

public class DatabaseLoaderImpl extends DatabaseLoader {

    private AddonManager<DatabaseAddon> addonManager;

    public DatabaseLoaderImpl(String addonsDir) throws IOException {
        this.addonManager = new AddonManagerImpl<>();
        this.addonManager.loadAddons(addonsDir, databaseAddon -> databaseAddon.databaseLoader = this);
        this.addonManager.enableAddons();
    }

    public void shutdown() {
        this.addonManager.disableAndUnloadAddons();
    }

    private void connectFailed(String name) {
        System.err.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.connectFailed").replace("%name%", name));
        SystemUtils.sleepUninterruptedly(5000);
        System.exit(0);
    }

    private boolean doConnect(DatabaseManager databaseManager, DatabaseConfig config) {
        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.connecting")
                .replace("%host%", config.getHost() + ":" + config.getPort()));
        try {
            if (databaseManager.connect(config)) {
                System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.successConnect")
                        .replace("%name%", databaseManager.getName())
                        .replace("%host%", config.getHost() + ":" + config.getPort()));
                return true;
            } else {
                System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.failedConnect")
                        .replace("%name%", databaseManager.getName())
                        .replace("%host%", config.getHost() + ":" + config.getPort()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void enableDatabase(DatabaseAddon addon, DatabaseManager databaseManager) {
        System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.loading")
                .replace("%name%", databaseManager.getName()).replace("%author%", addon.getAddonConfig().getAuthor()));

        DatabaseManager oldDatabaseManager = PeepoCloudNode.getInstance().getDatabaseManager();

        PeepoCloudNode.getInstance().setDatabaseManager(databaseManager);
        DatabaseConfig config = loadConfig(databaseManager);
        if (doConnect(databaseManager, config)) {
            oldDatabaseManager.shutdown();
            System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.successLoad")
                    .replace("%name%", databaseManager.getName()).replace("%author%", addon.getAddonConfig().getAuthor()));
        } else {
            if (oldDatabaseManager == null || !oldDatabaseManager.isConnected()) {
                connectFailed(databaseManager.getName());
            } else {
                System.out.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("database.loader.connectFailedUsingOld")
                        .replace("%name%", databaseManager.getName())
                        .replace("%old%", oldDatabaseManager.getName()));
            }
        }
    }

    @Override
    public DatabaseManager loadDatabaseManager() {
        Map<String, Pair<DatabaseManager, DatabaseAddon>> databases = new HashMap<>();
        this.addonManager.getLoadedAddons().values().forEach(databaseAddon -> {
            DatabaseManager databaseManager = databaseAddon.loadDatabaseManager();
            if (databaseManager != null) {
                databases.put(databaseManager.getName(), new Pair<>(databaseManager, databaseAddon));
            }
        });
        Arrays.asList(new MySQLDatabaseManager(), new ArangoDatabaseManager(), new MongoDatabaseManager())
                .forEach(databaseManager -> databases.put(databaseManager.getName(), new Pair<>(databaseManager, null)));

        DatabaseManager databaseManager = null;
        DatabaseAddon addon = null;

        if (PeepoCloudNode.getInstance().getInternalConfig().contains("databaseManager")) {
            for (Pair<DatabaseManager, DatabaseAddon> value : databases.values()) {
                if (value.getKey().getName().equalsIgnoreCase(PeepoCloudNode.getInstance().getInternalConfig().getString("databaseManager"))) {
                    databaseManager = value.getKey();
                    addon = value.getValue();
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
                        PeepoCloudNode.getInstance().getInternalConfig().append("databaseManager", setup.getData().getString("db").toLowerCase());
                        PeepoCloudNode.getInstance().saveInternalConfigFile();
                    });
            for (Pair<DatabaseManager, DatabaseAddon> value : databases.values()) {
                if (value.getKey().getName().equalsIgnoreCase(PeepoCloudNode.getInstance().getInternalConfig().getString("databaseManager"))) {
                    databaseManager = value.getKey();
                    addon = value.getValue();
                    break;
                }
            }
        }

        DatabaseConfig config = loadConfig(databaseManager);

        if (addon != null) {
            this.enableDatabase(addon, databaseManager);
        } else {
            if (!this.doConnect(databaseManager, config)) {
                connectFailed(databaseManager.getName());
            } else {
                PeepoCloudNode.getInstance().setDatabaseManager(databaseManager);
            }
        }

        return databaseManager;
    }

    @Override
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
