package net.peepocloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 09.12.2018
 */

import lombok.Getter;
import net.peepocloud.node.network.participant.BungeeCordParticipant;

@Getter
/**
 * Called when a bungee disconnects from this node
 */
public class BungeeDisconnectEvent extends NetworkBungeeEvent {
    private BungeeCordParticipant participant;
    public BungeeDisconnectEvent(BungeeCordParticipant participant) {
        super(participant.getProxyInfo());
        this.participant = participant;
    }
}
