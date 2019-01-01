package net.peepocloud.node.websocket;
/*
 * Created by Mc_Ruben on 01.01.2019
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.websocket.WebSocketClient;

import java.net.URI;

public class WebSocketClientImpl extends WebSocketClient {

    static {
        factory = new WebSocketClientFactory() {
            @Override
            public WebSocketClient createClient() {
                return new WebSocketClientImpl();
            }
        };
    }

    protected boolean connect0(URI uri) {
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

}
