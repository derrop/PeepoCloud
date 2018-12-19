package net.peepocloud.api.event.player;
/*
 * Created by Mc_Ruben on 01.12.2018
 */

import lombok.*;
import net.peepocloud.api.event.Cancellable;

@Getter
public class PlayerUpdateEvent extends OfflinePlayerEvent implements Cancellable {
    @Setter
    private boolean cancelled;
}
