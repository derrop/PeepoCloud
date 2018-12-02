package net.nevercloud.node.api.event.process.bungee;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.node.api.event.internal.Cancellable;
import net.nevercloud.node.api.event.process.ProcessEvent;
import net.nevercloud.node.server.process.BungeeProcess;

@Getter
/**
 * Called when a bungee was queued to startup on this node instance
 */
public class BungeeQueuedEvent extends ProcessEvent implements Cancellable {
    @Setter
    private boolean cancelled;
    private BungeeCordProxyInfo proxyInfo;

    public BungeeQueuedEvent(BungeeProcess process, BungeeCordProxyInfo proxyInfo) {
        super(process);
        this.proxyInfo = proxyInfo;
    }
}
