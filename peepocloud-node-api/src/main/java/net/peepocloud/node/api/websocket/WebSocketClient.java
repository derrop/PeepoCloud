package net.peepocloud.node.api.websocket;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.Getter;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.websocket.server.WebSocket;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public abstract class WebSocketClient {

    protected static WebSocketClientFactory factory;

    protected Collection<WebSocketHandler> handlers = new ArrayList<>();
    protected Channel channel;
    protected URI uri;
    protected boolean reconnect;
    protected int maxReconnectTries;
    protected boolean connected;
    protected ExecutorService executorService = Executors.newCachedThreadPool();

    WebSocket webSocket;

    public static WebSocketClient create() {
        return factory.createClient();
    }

    public void registerHandler(WebSocketHandler handler) {
        this.handlers.add(handler);
    }

    public void unregisterHandler(WebSocketHandler handler) {
        this.handlers.remove(handler);
    }

    public void connect(URI uri, boolean reconnect, int maxReconnectTries) {
        this.reconnect = reconnect;
        if (reconnect) {
            this.uri = uri;
            this.maxReconnectTries = maxReconnectTries;
            this.executorService.execute(() -> {
                while (!this.connect0(uri) && (this.maxReconnectTries == -1 || this.maxReconnectTries-- >= 0)) {
                    SystemUtils.sleepUninterruptedly(5000);
                }
            });
        } else {
            this.connect0(uri);
        }
    }

    public void connect(URI uri, boolean reconnect) {
        this.reconnect = reconnect;
        if (reconnect) {
            this.uri = uri;
            this.maxReconnectTries = -1;
            this.executorService.execute(() -> {
                while (!this.connect0(uri)) {
                    SystemUtils.sleepUninterruptedly(5000);
                }
            });
        } else {
            this.connect0(uri);
        }
    }

    public void connect(URI uri) {
        this.connect0(uri);
    }

    protected abstract boolean connect0(URI uri);

    public void close() {
        if (this.channel != null)
            this.channel.close().syncUninterruptibly();
        this.reconnect = false;
    }

    public ChannelFuture send(byte[] bytes) {
        return this.send(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes)));
    }

    public ChannelFuture send(String message) {
        return this.send(new TextWebSocketFrame(message));
    }

    public ChannelFuture send(WebSocketFrame webSocketFrame) {
        if (this.isConnected()) {
            return this.channel.writeAndFlush(webSocketFrame);
        }
        return null;
    }

    public boolean isConnected() {
        return this.connected && this.channel != null && this.channel.isOpen();
    }

    protected static abstract class WebSocketClientFactory {
        public abstract WebSocketClient createClient();
    }

}
