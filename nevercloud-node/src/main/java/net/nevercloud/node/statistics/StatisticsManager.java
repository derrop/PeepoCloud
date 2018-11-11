package net.nevercloud.node.statistics;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.Getter;
import net.nevercloud.lib.json.SimpleJsonObject;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.events.network.bungeecord.BungeeStartEvent;
import net.nevercloud.node.api.events.network.minecraftserver.ServerStartEvent;
import net.nevercloud.node.api.events.network.node.NodeConnectEvent;
import net.nevercloud.node.database.Database;
import net.nevercloud.node.events.EventHandler;

import java.util.ArrayList;
import java.util.Collection;

public class StatisticsManager {

    @Getter
    private SimpleJsonObject statistics;

    private Collection<StatisticsUpdateListener> listeners = new ArrayList<>();

    public void clearListeners() {
        this.listeners.clear();
    }

    public void addListener(StatisticsUpdateListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(StatisticsUpdateListener listener) {
        this.listeners.remove(listener);
    }

    @EventHandler
    private void serverStart(ServerStartEvent event) {
        this.update("serverStarts", 1);
    }

    @EventHandler
    private void bungeeStart(BungeeStartEvent event) {
        this.update("bungeeStarts", 1);
    }

    @EventHandler
    private void nodeConnect(NodeConnectEvent event) {
        this.update("nodeConnects", 1);
    }

    private Database getDatabase() {
        return NeverCloudNode.getInstance().getDatabaseManager().getDatabase("internal_configs");
    }

    private void update(String key, long add) {
        Database database = this.getDatabase();
        database.get("statistics", jsonObject -> {
            this.statistics = jsonObject;

            long oldValue = jsonObject.getLong(key);
            long newValue = oldValue + add;
            jsonObject.append(key, add);
            database.update("statistics", jsonObject);
            this.listeners.forEach(listener -> listener.call(key, oldValue, newValue));
        });
    }


    public static abstract class StatisticsUpdateListener {

        public abstract void call(String key, long oldValue, long newValue);

    }

}
