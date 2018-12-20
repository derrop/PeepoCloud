package net.peepocloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import lombok.Getter;
import net.peepocloud.node.api.event.network.minecraftserver.NetworkServerEvent;
import net.peepocloud.node.network.participant.MinecraftServerParticipant;

@Getter
/**
 * Called when a minecraft server disconnects from this node
 */
public class ServerDisconnectEvent extends NetworkServerEvent {
    private MinecraftServerParticipant participant;
    public ServerDisconnectEvent(MinecraftServerParticipant participant) {
        super(participant.getServerInfo());
        this.participant = participant;
    }
}
