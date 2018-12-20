package net.peepocloud.node.websocket.server.auth;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.node.websocket.server.WebSocket;

public interface WebSocketServerAuthMethod {

    boolean checkAuth(WebSocket webSocket, SimpleJsonObject jsonObject);

}
