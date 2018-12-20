package net.peepocloud.node.api.event.player;
/*
 * Created by Mc_Ruben on 01.12.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.peepocloud.node.api.event.Cancellable;

@Getter
public class PlayerUpdateEvent extends OfflinePlayerEvent implements Cancellable {
    @Setter
    private boolean cancelled;
}
