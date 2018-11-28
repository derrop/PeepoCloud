package net.nevercloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.node.api.event.network.NetworkEvent;

@Getter
@AllArgsConstructor
public class NetworkBungeeEvent extends NetworkEvent {
    private BungeeCordProxyInfo proxyInfo;
}
