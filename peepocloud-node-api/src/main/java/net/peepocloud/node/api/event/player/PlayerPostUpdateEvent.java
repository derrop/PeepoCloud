package net.peepocloud.node.api.event.player;
/*
 * Created by Mc_Ruben on 01.12.2018
 */

import net.peepocloud.lib.player.PeepoPlayer;

/**
 * Called after a player was updated
 */
public class PlayerPostUpdateEvent extends PlayerEvent {
    public PlayerPostUpdateEvent(PeepoPlayer player) {
        super(player);
    }
}
