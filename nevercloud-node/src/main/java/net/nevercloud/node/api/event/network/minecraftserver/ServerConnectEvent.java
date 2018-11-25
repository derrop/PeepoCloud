package net.nevercloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.node.api.event.internal.Event;
import net.nevercloud.node.network.participant.MinecraftServerParticipant;

@Getter
@AllArgsConstructor
public class ServerConnectEvent extends Event {
    private MinecraftServerParticipant participant;
}
