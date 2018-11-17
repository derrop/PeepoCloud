package net.nevercloud.node.api.events.internal;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
