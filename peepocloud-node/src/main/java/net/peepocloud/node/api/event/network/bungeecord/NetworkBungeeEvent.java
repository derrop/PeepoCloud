package net.peepocloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.*;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.api.event.network.NetworkEvent;

@Getter
@AllArgsConstructor
/**
 * Events that are called when something with a started/starting bungee happens
 */
public class NetworkBungeeEvent extends NetworkEvent {
    private BungeeCordProxyInfo proxyInfo;
}
