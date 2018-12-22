package net.peepocloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.Getter;
import net.peepocloud.node.api.network.BungeeCordParticipant;

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
