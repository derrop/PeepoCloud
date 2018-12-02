package net.nevercloud.node.statistic;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.Getter;
import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.event.network.bungeecord.BungeeStartEvent;
import net.nevercloud.node.api.event.network.minecraftserver.ServerStartEvent;
import net.nevercloud.node.api.event.network.node.NodeConnectEvent;
import net.nevercloud.node.database.Database;
import net.nevercloud.node.api.event.internal.EventHandler;

import java.util.ArrayList;
import java.util.Collection;

public class StatisticsManager {

    @Getter
    private SimpleJsonObject statistics;

    private Collection<StatisticsUpdateListener> listeners = new ArrayList<>();

    /**
     * Clears all registered listeners in this instance of {@link StatisticsManager}
     */
    public void clearListeners() {
        this.listeners.clear();
    }

    /**
     * Adds a {@link StatisticsUpdateListener} that is called every time an update is made in the statistics of this instance of {@link StatisticsManager}
     * @param listener the listener to add to this {@link StatisticsManager}
     */
    public void addListener(StatisticsUpdateListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a {@link StatisticsUpdateListener} out of this instance of {@link StatisticsManager}
     * @param listener the listener to to remove out of this {@link StatisticsManager}
     */
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
        database.get("statistic", jsonObject -> {
            if (jsonObject == null) {
                jsonObject = new SimpleJsonObject();
                long oldValue = jsonObject.contains(key) ? jsonObject.getLong(key) : 0;
                long newValue = oldValue + add;
                jsonObject.append(key, add);
                database.insert("statistic", jsonObject);
                this.listeners.forEach(listener -> listener.call(key, oldValue, newValue));
            } else {
                long oldValue = jsonObject.contains(key) ? jsonObject.getLong(key) : 0;
                long newValue = oldValue + add;
                jsonObject.append(key, add);
                database.update("statistic", jsonObject);
                this.listeners.forEach(listener -> listener.call(key, oldValue, newValue));
            }
            this.statistics = jsonObject;

        });
    }

    /**
     * A Listener that is called every time an update is made in the statistics of the instance of {@link StatisticsManager} to which it was added
     * @see StatisticsManager#addListener(StatisticsUpdateListener)
     */
    public static abstract class StatisticsUpdateListener {

        public abstract void call(String key, long oldValue, long newValue);

    }

}
