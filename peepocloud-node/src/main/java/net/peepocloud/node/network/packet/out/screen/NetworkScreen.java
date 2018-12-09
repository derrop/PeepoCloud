package net.peepocloud.node.network.packet.out.screen;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.peepocloud.node.network.participant.NodeParticipant;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Data
@ToString
@EqualsAndHashCode
public class NetworkScreen {

    public NetworkScreen(NodeParticipant participant) {
        this.participant = participant;
    }

    private NodeParticipant participant;
    private Map<UUID, Consumer<String>> consumers = new HashMap<>();

    public void call(String line) {
        this.consumers.values().forEach(consumer -> consumer.accept(line));
    }
}
