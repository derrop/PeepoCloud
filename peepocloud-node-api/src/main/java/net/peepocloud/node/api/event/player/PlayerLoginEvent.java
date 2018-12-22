package net.peepocloud.node.api.event.player;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.Getter;
import net.peepocloud.lib.player.PeepoPlayer;

@Getter
/**
 * Called after a player is successfully logged in to the network
 */
public class PlayerLoginEvent extends PlayerEvent {
    public PlayerLoginEvent(PeepoPlayer player) {
        super(player);
    }
}
