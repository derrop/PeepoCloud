package net.nevercloud.node.api.events.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.node.events.Event;
import net.nevercloud.node.network.participants.MinecraftServerParticipant;

@Getter
@AllArgsConstructor
public class ServerConnectEvent extends Event {
    private MinecraftServerParticipant participant;
}