package net.nevercloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.node.api.event.internal.Event;
import net.nevercloud.node.network.participant.BungeeCordParticipant;

@Getter
@AllArgsConstructor
public class BungeeStopEvent extends Event {
    private BungeeCordParticipant participant;
}
