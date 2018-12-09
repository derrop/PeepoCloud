package net.peepocloud.node.websocket;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public abstract class WebSocketHandler {

    public abstract void handleRequest(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame webSocketFrame) throws Exception;

}
