package net.peepocloud.node.api.statistic;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.Getter;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.PeepoCloudNodeAPI;
import net.peepocloud.node.api.database.Database;
import net.peepocloud.node.api.event.EventHandler;
import net.peepocloud.node.api.event.network.bungeecord.BungeeStartEvent;
import net.peepocloud.node.api.event.network.minecraftserver.ServerStartEvent;
import net.peepocloud.node.api.event.network.node.NodeConnectEvent;
import net.peepocloud.node.api.websocket.WebSocketClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

public class StatisticsManager {

    @Getter
    private SimpleJsonObject statistics;

    private WebSocketClient webSocketClient;

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

    /**
     * Reloads this {@link StatisticsManager} and connects to the Server if {@code enableGlobalStats} is true and if it's not connected or disconnect if it's connected and {@code enableGlobalStats} is false
     * @param enableGlobalStats defines if the global stats system should be used
     */
    public void reload(boolean enableGlobalStats) {
        boolean connected = this.webSocketClient != null && this.webSocketClient.isConnected();
        if (enableGlobalStats && !connected) {
            this.webSocketClient = WebSocketClient.create();
            this.webSocketClient.connect(URI.create(SystemUtils.CENTRAL_SERVER_URL_WS_GSTATS), true);
        }
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
        return PeepoCloudNodeAPI.getInstance().getDatabaseManager().getDatabase("internal_configs");
    }

    private void update(String key, long add) {
        Database database = this.getDatabase();
        database.get("statistic").thenAccept(jsonObject -> {
            if (jsonObject == null) {
                jsonObject = new SimpleJsonObject();
                long oldValue = jsonObject.contains(key) ? jsonObject.getLong(key) : 0;
                long newValue = oldValue + add;
                jsonObject.append(key, newValue);
                database.insertAsync("statistic", jsonObject);
                this.listeners.forEach(listener -> listener.call(key, oldValue, newValue));
            } else {
                long oldValue = jsonObject.contains(key) ? jsonObject.getLong(key) : 0;
                long newValue = oldValue + add;
                jsonObject.append(key, newValue);
                database.updateAsync("statistic", jsonObject);
                this.listeners.forEach(listener -> listener.call(key, oldValue, newValue));
            }
            this.statistics = jsonObject;

            this.sendGlobalStatsUpdate(key, add);
        });
    }

    private void sendGlobalStatsUpdate(String key, long val) {
        if (PeepoCloudNodeAPI.getInstance().getCloudConfig().isUseGlobalStats()) {
            if (this.webSocketClient != null) {
                this.webSocketClient.send(new SimpleJsonObject().append("update", key).append("val", val).toJson());
            }
        } else if (this.webSocketClient != null && this.webSocketClient.isConnected()) {
            this.webSocketClient.close();
        }
    }

    /**
     * A Listener that is called every time an update is made in the statistics of the instance of {@link StatisticsManager} to which it was added
     * @see StatisticsManager#addListener(StatisticsUpdateListener)
     */
    public static abstract class StatisticsUpdateListener {

        public abstract void call(String key, long oldValue, long newValue);

    }

}
