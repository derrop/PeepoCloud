package net.peepocloud.node.api.websocket.server;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.peepocloud.lib.config.json.SimpleJsonObject;

import java.util.function.Consumer;

public abstract class JsonServerWebSocketHandler extends ServerWebSocketHandler {
    @Override
    public final void handleRequest(WebSocket webSocket, TextWebSocketFrame webSocketFrame, Consumer<WebSocketFrame> responseConsumer) throws Exception {
        this.handleJson(webSocket, new SimpleJsonObject(webSocketFrame.text()), jsonObject -> responseConsumer.accept(new TextWebSocketFrame(jsonObject.toJson())));
    }

    public abstract void handleJson(WebSocket webSocket, SimpleJsonObject jsonObject, Consumer<SimpleJsonObject> responseConsumer) throws Exception;
}
