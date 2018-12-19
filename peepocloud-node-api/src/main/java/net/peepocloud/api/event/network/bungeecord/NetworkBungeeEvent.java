package net.peepocloud.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.api.event.network.NetworkEvent;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;

@Getter
@AllArgsConstructor
/**
 * Events that are called when something with a started/starting bungee happens
 */
public class NetworkBungeeEvent extends NetworkEvent {
    private BungeeCordProxyInfo proxyInfo;
}
