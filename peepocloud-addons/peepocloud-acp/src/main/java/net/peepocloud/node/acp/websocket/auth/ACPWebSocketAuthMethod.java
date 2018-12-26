package net.peepocloud.node.acp.websocket.auth;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.websocket.server.WebSocket;
import net.peepocloud.node.websocket.server.auth.SynchronizedWebSocketServerAuthMethod;

import java.util.function.Consumer;

public class ACPWebSocketAuthMethod implements SynchronizedWebSocketServerAuthMethod {
    @Override
    public void checkAuthSynchronized(WebSocket webSocket, SimpleJsonObject jsonObject, Consumer<Boolean> consumer) {
        if (!jsonObject.contains("username") || !jsonObject.contains("password")) {
            consumer.accept(false);
            return;
        }

        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");

        if (username == null || password == null) {
            consumer.accept(false);
            return;
        }

        PeepoCloudNode.getInstance().getUserManager().checkCredentials(username, password, aBoolean -> {
            consumer.accept(aBoolean);
            if (aBoolean) {
                webSocket.send(new SimpleJsonObject().append("success", true).toJson());
            }
        });
    }
}
