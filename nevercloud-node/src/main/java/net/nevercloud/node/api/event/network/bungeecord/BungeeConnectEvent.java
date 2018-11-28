package net.nevercloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.node.api.event.internal.Event;
import net.nevercloud.node.network.participant.BungeeCordParticipant;

@Getter
public class BungeeConnectEvent extends NetworkBungeeEvent {
    private BungeeCordParticipant participant;

    public BungeeConnectEvent(BungeeCordParticipant participant) {
        super(participant.getProxyInfo());
        this.participant = participant;
    }
}
