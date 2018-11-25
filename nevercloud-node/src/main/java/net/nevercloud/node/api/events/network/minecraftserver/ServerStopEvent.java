package net.nevercloud.node.api.events.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.node.api.events.internal.Event;
import net.nevercloud.node.network.participant.MinecraftServerParticipant;

@Getter
@AllArgsConstructor
public class ServerStopEvent extends Event {
    private MinecraftServerParticipant participant;
}
