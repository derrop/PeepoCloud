package net.nevercloud.node.api.events.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.MinecraftServerInfo;
import net.nevercloud.node.events.Event;

@Getter
@AllArgsConstructor
public class ServerStartEvent extends Event {
    private MinecraftServerInfo minecraftServerInfo;
}