package net.peepocloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.api.event.network.minecraftserver.NetworkServerEvent;
import net.peepocloud.node.network.participant.MinecraftServerParticipant;

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
