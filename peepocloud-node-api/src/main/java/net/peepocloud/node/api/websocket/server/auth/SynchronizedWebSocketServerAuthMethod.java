package net.peepocloud.node.api.websocket.server.auth;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.websocket.server.WebSocket;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public interface SynchronizedWebSocketServerAuthMethod extends WebSocketServerAuthMethod {
    @Override
    default boolean checkAuth(WebSocket webSocket, SimpleJsonObject jsonObject) {
        AtomicReference<Boolean> a = new AtomicReference<>(null);
        checkAuthSynchronized(webSocket, jsonObject, a::set);
        while (a.get() == null) {
            SystemUtils.sleepUninterruptedly(0, 500000);
        }
        return a.get();
    }

    void checkAuthSynchronized(WebSocket webSocket, SimpleJsonObject jsonObject, Consumer<Boolean> consumer);
}
