package net.peepocloud.node.websocket;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import lombok.Getter;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.websocket.server.WebSocket;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class WebSocketClient {

    private Collection<WebSocketHandler> handlers = new ArrayList<>();
    private Channel channel;
    private URI uri;
    private boolean reconnect;
    private int maxReconnectTries;
    private boolean connected;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    WebSocket webSocket;

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

    private boolean connect0(URI uri) {
        System.out.println("Trying to connect web socket to " + uri + "...");
        try {
            int port = uri.getPort();
            String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
            if (port == -1)
                port = scheme.equalsIgnoreCase("ws") ? 80 : scheme.equalsIgnoreCase("wss") ? 443 : -1;

            boolean epoll = Epoll.isAvailable();
            EventLoopGroup eventLoopGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();

            WebSocketClientHandler handler = new WebSocketClientHandler(
                    this,
                    WebSocketClientHandshakerFactory.newHandshaker(
                            uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()
                    )
            );

            this.channel = new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(epoll ? EpollSocketChannel.class : NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(
                                            new HttpClientCodec(),
                                            new HttpObjectAggregator(Integer.MAX_VALUE),
                                            handler
                                    );
                        }
                    })
                    .connect(uri.getHost(), port)
                    .syncUninterruptibly()
                    .channel();
            handler.getChannelPromise().syncUninterruptibly();


            channel.writeAndFlush(new TextWebSocketFrame(
                    new SimpleJsonObject()
                            .append("user", PeepoCloudNode.getInstance().getCloudConfig().getUsername())
                            .append("apiToken", PeepoCloudNode.getInstance().getCloudConfig().getApiToken())
                            .toJson()
            ))
                    .syncUninterruptibly();
            this.connected = this.channel != null && this.channel.isOpen();
        } catch (Exception e) {
            System.out.println("WebSocketClient connect error: " + e.getClass().getName() + " " + e.getMessage());
            this.connected = false;
        }
        return this.connected;
    }

    void handleDisconnected() {
        if (this.connected && this.reconnect && (this.maxReconnectTries > 0 || this.maxReconnectTries == -1)) {
            this.connected = false;
            this.executorService.execute(() -> {
                SystemUtils.sleepUninterruptedly(5000);
                while (!this.connect0(uri) && (maxReconnectTries == -1 || maxReconnectTries-- >= 0)) {
                    SystemUtils.sleepUninterruptedly(5000);
                }
            });
        }
    }

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

}
