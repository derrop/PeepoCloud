package net.peepocloud.node.websocket;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import lombok.*;
import net.peepocloud.node.api.websocket.WebSocketClient;
import net.peepocloud.node.api.websocket.WebSocketHandler;

@Getter
class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketClientImpl webSocketClient;
    private WebSocketClientHandshaker handshaker;
    private ChannelPromise channelPromise;

    WebSocketClientHandler(WebSocketClientImpl webSocketClient, WebSocketClientHandshaker handshaker) {
        this.webSocketClient = webSocketClient;
        this.handshaker = handshaker;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channelPromise = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Got disconnected from WebSocket!");
        this.webSocketClient.handleDisconnected();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(channelHandlerContext.channel(), (FullHttpResponse) o);
                System.out.println("WebSocket Client connected!");
                channelPromise.setSuccess();
            } catch (WebSocketHandshakeException e) {
                System.out.println("WebSocket handshake failed");
                channelPromise.setFailure(e);
                e.printStackTrace();
            }
            return;
        }

        if (!(o instanceof TextWebSocketFrame)) {
            return;
        }

        TextWebSocketFrame webSocketFrame = (TextWebSocketFrame) o;

        for (WebSocketHandler handler : this.webSocketClient.getHandlers()) {
            handler.handleRequest(channelHandlerContext, webSocketFrame, response -> channelHandlerContext.channel().writeAndFlush(response));
        }
    }
}
