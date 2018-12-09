package net.peepocloud.node.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.peepocloud.node.addon.Addon;

public abstract class DatabaseAddon extends Addon {

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

    public abstract DatabaseManager loadDatabaseManager();
}
