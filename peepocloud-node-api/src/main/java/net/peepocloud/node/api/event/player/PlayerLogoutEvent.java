package net.peepocloud.node.api.event.player;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import net.peepocloud.lib.player.PeepoPlayer;

public class PlayerLogoutEvent extends PlayerEvent {
    public PlayerLogoutEvent(PeepoPlayer player) {
        super(player);
    }
}
