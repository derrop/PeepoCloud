package net.nevercloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.api.event.internal.Event;
import net.nevercloud.node.network.participant.MinecraftServerParticipant;

@Getter
/**
 * Called when a server is stopped on this node instance
 */
public class ServerStopEvent extends NetworkServerEvent {
    public ServerStopEvent(MinecraftServerInfo serverInfo) {
        super(serverInfo);
    }
}
