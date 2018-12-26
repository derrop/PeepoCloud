package net.peepocloud.node.acp;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import lombok.Getter;
import net.peepocloud.node.acp.handler.ACPWebSocketDefaultHandler;
import net.peepocloud.node.acp.listener.ACPDashboardListener;
import net.peepocloud.node.acp.websocket.auth.ACPWebSocketAuthMethod;
import net.peepocloud.node.api.addon.node.NodeAddon;
import net.peepocloud.node.websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class PeepoACP extends NodeAddon {

    @Getter
    private static PeepoACP instance;

    private WebSocketServer webSocketServer;

    private ACPConfig config;

    @Override
    public void onLoad() {
        instance = this;

        this.config = new ACPConfig(this.getConfigFile()).load();
    }

    @Override
    public void onEnable() {
        this.webSocketServer = new WebSocketServer(new ACPWebSocketAuthMethod());

        SocketAddress address = this.config.getHost() == null || this.config.getHost().equals("*") ?
                new InetSocketAddress(this.config.getPort()) :
                new InetSocketAddress(this.config.getHost(), this.config.getPort());

        this.webSocketServer.bind(address);

        this.webSocketServer.registerHandler(new ACPWebSocketDefaultHandler());

        this.getNode().getEventManager().registerListener(new ACPDashboardListener(this.webSocketServer));

    }

    @Override
    public void onDisable() {
        this.webSocketServer.close();
        instance = null;
    }
}
