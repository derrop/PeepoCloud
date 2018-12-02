package net.nevercloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.api.event.internal.Event;
import net.nevercloud.node.network.NetworkServer;
import net.nevercloud.node.network.participant.MinecraftServerParticipant;

@Getter
/**
 * Called when a server is connected to this node instance
 */
public class ServerConnectEvent extends NetworkServerEvent {
    private MinecraftServerParticipant participant;

    public ServerConnectEvent(MinecraftServerParticipant participant) {
        super(participant.getServerInfo());
        this.participant = participant;
    }
}
