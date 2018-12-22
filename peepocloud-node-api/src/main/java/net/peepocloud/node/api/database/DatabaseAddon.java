package net.peepocloud.node.api.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.google.common.base.Preconditions;
import net.peepocloud.node.api.addon.Addon;

public abstract class DatabaseAddon extends Addon {

    public DatabaseLoader databaseLoader;

    public void setDatabaseLoader(DatabaseLoader databaseLoader) {
        Preconditions.checkArgument(this.databaseLoader == null, "databaseLoader already set");
        this.databaseLoader = databaseLoader;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public abstract DatabaseManager loadDatabaseManager();
}
