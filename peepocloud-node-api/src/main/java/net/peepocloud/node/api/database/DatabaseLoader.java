package net.peepocloud.node.api.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

public abstract class DatabaseLoader {

    public abstract void shutdown();

    public abstract DatabaseManager loadDatabaseManager();

    public abstract DatabaseConfig loadConfig(DatabaseManager databaseManager);

}
