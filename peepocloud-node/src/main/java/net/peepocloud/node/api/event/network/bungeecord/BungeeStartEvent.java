package net.peepocloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;

@Getter
/**
 * Called when a bungee was started on one node in the network
 */
public class BungeeStartEvent extends NetworkBungeeEvent {
    public BungeeStartEvent(BungeeCordProxyInfo proxyInfo) {
        super(proxyInfo);
    }
}
