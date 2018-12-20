package net.peepocloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.node.network.participant.BungeeCordParticipant;

@Getter
/**
 * Called when a bungee is connected to this node instance
 */
public class BungeeConnectEvent extends NetworkBungeeEvent {
    private BungeeCordParticipant participant;

    public BungeeConnectEvent(BungeeCordParticipant participant) {
        super(participant.getProxyInfo());
        this.participant = participant;
    }
}
