package net.nevercloud.node.api.events.player;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.node.api.events.internal.Cancellable;
import net.nevercloud.node.api.events.internal.Event;

@Getter
@AllArgsConstructor
public class PlayerPreLoginEvent extends Event implements Cancellable {
    //TODO add player
    @Setter
    private boolean cancelled;
}
