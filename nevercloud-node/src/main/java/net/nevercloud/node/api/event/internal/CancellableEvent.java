package net.nevercloud.node.api.event.internal;
/*
 * Created by Mc_Ruben on 06.12.2018
 */

import lombok.Data;

@Data
public class CancellableEvent extends Event implements Cancellable {
    private boolean cancelled;
}
