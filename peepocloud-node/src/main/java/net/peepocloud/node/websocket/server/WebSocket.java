package net.peepocloud.node.websocket.server;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class WebSocket {

    @Setter
    private String username;
    @Setter
    private String uniqueId;
    @Setter(AccessLevel.PACKAGE)
    private boolean authSuccessful;
    private Channel channel;

    public void send(WebSocketFrame webSocketFrame) {
        if (this.isConnected()) {
            this.channel.writeAndFlush(webSocketFrame);
        }
    }

    public void send(String message) {
        this.send(new TextWebSocketFrame(message));
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

}
