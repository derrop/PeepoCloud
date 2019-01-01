package net.peepocloud.node.api.websocket.server;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.function.Consumer;

public abstract class ServerWebSocketHandler {

    public abstract void handleRequest(WebSocket webSocket, TextWebSocketFrame webSocketFrame, Consumer<WebSocketFrame> responseConsumer) throws Exception;

}
