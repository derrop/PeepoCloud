package net.peepocloud.node.api.event.player;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.node.api.event.internal.Cancellable;

@Getter
/**
 * Called when a player logs in to the network
 */
public class PlayerPreLoginEvent extends PlayerEvent implements Cancellable {
    //TODO add player
    @Setter
    private boolean cancelled;
}
