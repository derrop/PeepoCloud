package net.nevercloud.node.api.event.player;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.node.api.event.internal.Cancellable;
import net.nevercloud.node.api.event.internal.Event;

@Getter
/**
 * Called when a player logs in to the network
 */
public class PlayerPreLoginEvent extends PlayerEvent implements Cancellable {
    //TODO add player
    @Setter
    private boolean cancelled;
}
