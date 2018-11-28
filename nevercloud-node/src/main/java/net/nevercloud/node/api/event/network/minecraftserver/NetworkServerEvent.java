package net.nevercloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.api.event.network.NetworkEvent;

@Getter
@AllArgsConstructor
public class NetworkServerEvent extends NetworkEvent {
    private MinecraftServerInfo serverInfo;
}
