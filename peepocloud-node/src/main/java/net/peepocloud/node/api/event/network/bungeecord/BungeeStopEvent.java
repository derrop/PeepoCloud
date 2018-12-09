package net.peepocloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

@Getter
/**
 * Called when a bungee on this node instance is stopped
 */
public class BungeeStopEvent extends NetworkBungeeEvent {
    public BungeeStopEvent(BungeeCordProxyInfo proxyInfo) {
        super(proxyInfo);
    }
}
