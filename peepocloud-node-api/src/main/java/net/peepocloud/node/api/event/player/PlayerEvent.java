package net.peepocloud.node.api.event.player;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.lib.player.PeepoPlayer;

@Getter
@AllArgsConstructor
public class PlayerEvent {
    private PeepoPlayer player;
}
