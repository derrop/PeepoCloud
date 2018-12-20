package net.peepocloud.node.websocket.server;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import com.google.common.base.Preconditions;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.Getter;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.node.websocket.server.auth.WebSocketServerAuthMethod;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WebSocketServer {

    @Getter
    private Collection<ServerWebSocketHandler> handlers = new ArrayList<>();
    private EventLoopGroup bossGroup, workerGroup;

    @Getter
    private Map<Channel, WebSocket> webSockets = new HashMap<>();

    private WebSocketServerAuthMethod authMethod;

    public WebSocketServer(WebSocketServerAuthMethod authMethod) {
        this.authMethod = Preconditions.checkNotNull(authMethod);
    }

    public void registerHandler(ServerWebSocketHandler handler) {
        this.handlers.add(handler);
    }

    public void unregisterHandler(ServerWebSocketHandler handler) {
        this.handlers.remove(handler);
    }

    public void bind(String host, int port) {
        this.bind(new InetSocketAddress(host, port));
    }

    public void bind(int port) {
        this.bind(new InetSocketAddress(port));
    }

    public void bind(SocketAddress address) {
        boolean epoll = Epoll.isAvailable();
        bossGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        workerGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        new ServerBootstrap()
                .channel(epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .group(bossGroup, workerGroup)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(
                                        new HttpServerCodec(),
                                        new HttpObjectAggregator(65535),
                                        //new WebSocketServerCompressionHandler(),
                                        new WebSocketServerProtocolHandler("/"),
                                        new SimpleChannelInboundHandler<TextWebSocketFrame>() {
                                            @Override
                                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
                                                WebSocket webSocket = webSockets.get(channelHandlerContext.channel());
                                                if (webSocket == null) {
                                                    channelHandlerContext.channel().close();
                                                    return;
                                                }
                                                if (!webSocket.isAuthSuccessful()) {
                                                    try {
                                                        SimpleJsonObject jsonObject = new SimpleJsonObject(textWebSocketFrame.text());
                                                        if (!authMethod.checkAuth(webSocket, jsonObject)) {
                                                            webSockets.remove(channelHandlerContext.channel());
                                                            channelHandlerContext.channel().close();
                                                            return;
                                                        }
                                                        webSocket.setAuthSuccessful(true);
                                                    } catch (Exception e) {
                                                        webSockets.remove(channelHandlerContext.channel());
                                                        channelHandlerContext.channel().close();
                                                    }
                                                    return;
                                                }

                                                for (ServerWebSocketHandler handler : getHandlers()) {
                                                    handler.handleRequest(webSocket, textWebSocketFrame, webSocketFrame -> channelHandlerContext.channel().writeAndFlush(webSocketFrame));
                                                }
                                            }

                                            @Override
                                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                                if (!(cause instanceof IOException)) {
                                                    super.exceptionCaught(ctx, cause);
                                                }
                                            }

                                            @Override
                                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                                super.channelInactive(ctx);
                                                webSockets.remove(ctx.channel());
                                            }

                                            @Override
                                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                                super.channelActive(ctx);
                                                webSockets.put(ctx.channel(), new WebSocket(null, null, false, ctx.channel()));
                                            }
                                        }
                                );
                    }
                })
                .bind(address);

        System.out.println("&aBound WebSocketServer &7@" + address);
    }

    public void close() {
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }


}
