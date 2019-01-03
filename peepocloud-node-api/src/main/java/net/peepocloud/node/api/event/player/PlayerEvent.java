package net.peepocloud.node.api.event.player;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.node.api.event.Event;

@Getter
@AllArgsConstructor
public class PlayerEvent extends Event {
    private PeepoPlayer player;
}
