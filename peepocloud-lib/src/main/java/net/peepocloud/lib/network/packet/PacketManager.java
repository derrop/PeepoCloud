package net.peepocloud.lib.network.packet;


import net.peepocloud.lib.network.NetworkParticipant;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class PacketManager {
    private Map<Integer, PacketInfo> registeredPackets = new HashMap<>();
    private Map<UUID, Consumer<Packet>> pendingQueries = new HashMap<>();

    public void registerPacket(PacketInfo packetInfo) {
        this.registeredPackets.put(packetInfo.getId(), packetInfo);
    }

    public PacketInfo getPacketInfo(int id) {
        return this.registeredPackets.get(id);
    }

    public Packet convertToQueryPacket(Packet packet, UUID uuid) {
        packet.setQueryUUID(uuid);
        return packet;
    }

    /**
     * Sends a query and accepts the {@code result} when the value is available
     *
     * @param networkParticipant the participant in the network, where the packet will be send to
     * @param packet the query-packet
     * @param result result-packet of the query
     */

    public void packetQueryAsync(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> result) {
        networkParticipant.sendPacket(packet);
        this.pendingQueries.put(packet.getQueryUUID(), result);
    }

    /**
     * Sends a query and waits for the result
     *
     * @param networkParticipant the participant in the network, where the packet will be send to
     * @param packet the query-packet
     * @return the result-packet of the query
     */

    public Packet packetQuery(NetworkParticipant networkParticipant, Packet packet) {
        AtomicReference<Packet> reference = new AtomicReference<>();
        Object lock = new Object();

        this.packetQueryAsync(networkParticipant, packet, result -> {
            reference.set(result);
            synchronized (lock) {
                lock.notify();
            }
        });

        try {
            synchronized (lock) {
                lock.wait(TimeUnit.SECONDS.toMillis(6));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return reference.get();
    }

    public Consumer<Packet> getQueryAndRemove(UUID uuid) {
        return this.pendingQueries.remove(uuid);
    }


}
