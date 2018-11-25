package net.nevercloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.BungeeCordProxyInfo;
import net.nevercloud.node.api.event.internal.Cancellable;
import net.nevercloud.node.api.event.internal.Event;

@Getter
@RequiredArgsConstructor
public class BungeeQueuedEvent extends Event implements Cancellable {
    private final BungeeCordProxyInfo bungeeCordProxyInfo;
    @Setter
    private boolean cancelled;
}
