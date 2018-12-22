package net.peepocloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.Getter;
import net.peepocloud.node.api.network.MinecraftServerParticipant;

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
