package net.peepocloud.node.api.event.process.bungee;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.api.event.internal.Cancellable;
import net.peepocloud.node.api.event.process.ProcessEvent;
import net.peepocloud.node.server.process.BungeeProcess;

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
