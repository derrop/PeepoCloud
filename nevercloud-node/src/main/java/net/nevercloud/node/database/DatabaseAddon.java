package net.nevercloud.node.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.nevercloud.node.addon.Addon;

public class DatabaseAddon extends Addon {

    protected DatabaseLoader databaseLoader;

    @Override
    protected void onLoad() {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    protected void enableDatabaseManager(DatabaseManager databaseManager) {
        this.databaseLoader.enableDatabase(this, databaseManager);
    }
}