package net.peepocloud.api.event;
/*
 * Created by Mc_Ruben on 06.12.2018
 */

import lombok.Data;

@Data
public class CancellableEvent extends Event implements Cancellable {
    private boolean cancelled;
}
