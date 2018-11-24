package net.nevercloud.node.api.events.network.bungeecord;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.BungeeCordProxyInfo;
import net.nevercloud.node.api.events.internal.Event;

@Getter
@AllArgsConstructor
public class BungeeStartEvent extends Event {
    private BungeeCordProxyInfo bungeeCordProxyInfo;
}
