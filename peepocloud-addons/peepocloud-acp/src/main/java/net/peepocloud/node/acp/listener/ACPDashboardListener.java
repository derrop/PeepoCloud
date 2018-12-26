package net.peepocloud.node.acp.listener;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import lombok.AllArgsConstructor;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.users.User;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.event.EventHandler;
import net.peepocloud.node.api.event.network.minecraftserver.ServerStartEvent;
import net.peepocloud.node.api.event.network.minecraftserver.ServerStopEvent;
import net.peepocloud.node.api.event.player.PlayerLoginEvent;
import net.peepocloud.node.api.event.player.PlayerLogoutEvent;
import net.peepocloud.node.api.event.user.UserCreateEvent;
import net.peepocloud.node.api.event.user.UserDeleteEvent;
import net.peepocloud.node.api.event.user.UserUpdateEvent;
import net.peepocloud.node.websocket.server.WebSocketServer;

@AllArgsConstructor
public class ACPDashboardListener {

    private WebSocketServer webSocketServer;

    @EventHandler
    private void handlePlayerLogin(PlayerLoginEvent event) {
        this.webSocketServer.getWebSockets().values().forEach(webSocket -> {
            webSocket.send(
                    new SimpleJsonObject().append("key", "onlineCountUpdate").append("data", new SimpleJsonObject().append("onlineCount", 0).asJsonObject()).toJson() //TODO
            );
        });
    }

    @EventHandler
    private void handlePlayerLogout(PlayerLogoutEvent event) {
        this.webSocketServer.getWebSockets().values().forEach(webSocket -> {
            webSocket.send(
                    new SimpleJsonObject().append("key", "onlineCountUpdate").append("data", new SimpleJsonObject().append("onlineCount", 0).asJsonObject()).toJson() //TODO
            );
        });
    }

    @EventHandler
    public void handleServerQueued(ServerStartEvent event) {
        this.sendMemoryUpdate(PeepoCloudNode.getInstance().getMemoryUsed());
        this.webSocketServer.getWebSockets().values().forEach(webSocket -> {
            webSocket.send(
                    new SimpleJsonObject().append("key", "onlineServersUpdate").append("data", new SimpleJsonObject().append("onlineCount", PeepoCloudNode.getInstance().getMinecraftServers().size()).asJsonObject()).toJson()
            );
        });
    }

    @EventHandler
    private void handleServerStop(ServerStopEvent event) {
        this.webSocketServer.getWebSockets().values().forEach(webSocket -> {
            webSocket.send(
                    new SimpleJsonObject().append("key", "onlineServersUpdate").append("data", new SimpleJsonObject().append("onlineCount", PeepoCloudNode.getInstance().getMinecraftServers().size()).asJsonObject()).toJson()
            );
        });
        this.sendMemoryUpdate(PeepoCloudNode.getInstance().getMemoryUsed());
    }

    @EventHandler
    private void handleUserUpdate(UserUpdateEvent event) {
        this.sendUserUpdate(event.getUser());
    }

    @EventHandler
    private void handleUserCreate(UserCreateEvent event) {
        this.sendUserUpdate(event.getUser());
    }

    private void sendUserUpdate(User user) {
        this.webSocketServer.getWebSockets().values().forEach(webSocket -> {
            webSocket.send(
                    new SimpleJsonObject().append("key", "addUser").append("data",
                            new SimpleJsonObject()
                                    .append("username", user.getUsername())
                                    .append("apiToken", user.getApiToken())
                                    .append("iconUrl", user.getMetaData().getString("iconUrl"))
                                    .asJsonObject()
                    ).toJson()
            );
        });
    }

    @EventHandler
    private void handleUserDelete(UserDeleteEvent event) {
        this.webSocketServer.getWebSockets().values().forEach(webSocket -> {
            webSocket.send(
                    new SimpleJsonObject().append("key", "deleteUser").append("data", new SimpleJsonObject().append("username", event.getUser().getUsername()).asJsonObject()).toJson()
            );
        });
    }

    private void sendMemoryUpdate(int newUsage) {
        this.webSocketServer.getWebSockets().values().forEach(webSocket -> {
            webSocket.send(
                    new SimpleJsonObject().append("key", "memoryUpdate").append("data", new SimpleJsonObject().append("usage", newUsage).append("max", PeepoCloudNode.getInstance().getMaxMemory()).asJsonObject()).toJson()
            );
        });
    }

}
