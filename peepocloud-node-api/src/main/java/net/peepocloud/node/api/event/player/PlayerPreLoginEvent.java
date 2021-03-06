package net.peepocloud.node.api.event.player;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.node.api.event.Cancellable;

@Getter
/**
 * Called when a player logs in to the network
 */
public class PlayerPreLoginEvent extends PlayerEvent implements Cancellable {
    @Setter
    private boolean cancelled;
    @Setter
    private String cancelReason;

    public PlayerPreLoginEvent(PeepoPlayer player) {
        super(player);
    }
}
