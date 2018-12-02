package net.nevercloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.node.api.event.internal.Event;

@Getter
/**
 * Called when a bungee was started on this node instance
 */
public class BungeeStartEvent extends NetworkBungeeEvent {
    public BungeeStartEvent(BungeeCordProxyInfo proxyInfo) {
        super(proxyInfo);
    }
}
