package net.nevercloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.api.event.internal.Cancellable;
import net.nevercloud.node.api.event.internal.Event;

@Getter
@RequiredArgsConstructor
public class ServerQueuedEvent extends Event implements Cancellable {
    private final MinecraftServerInfo minecraftServerInfo;
    @Setter
    private boolean cancelled;
}
