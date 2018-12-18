package net.peepocloud.lib.network.packet;


import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.handler.PacketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class PacketManager {
    private Map<Integer, PacketInfo> registeredPackets = new HashMap<>();
    private Map<UUID, CompletableFuture<Packet>> pendingQueries = new HashMap<>();

    public void registerPacket(PacketInfo packetInfo) {
        this.registeredPackets.put(packetInfo.getId(), packetInfo);
    }

    public void registerPacket(PacketHandler<? extends Packet> handler) {
        this.registerPacket(new PacketInfo(handler.getId(), handler.getPacketClass(), handler));
    }

    public void clearPacketHandlers() {
        this.registeredPackets.clear();
    }

    public Map<Integer, PacketInfo> getRegisteredPackets() {
        return registeredPackets;
    }

    public PacketInfo getPacketInfo(int id) {
        return this.registeredPackets.get(id);
    }

    public Packet convertToQueryPacket(Packet packet, UUID uuid) {
        packet.setQueryUUID(uuid);
        return packet;
    }

    /**
     * Sends a query and completes the future when the value is available
     *
     * @param networkParticipant the participant in the network, where the packet will be send to
     * @param packet the query-packet
     * @return future for the packet
     */

    public CompletableFuture<Packet> packetQueryAsync(NetworkParticipant networkParticipant, Packet packet) {
        this.convertToQueryPacket(packet, UUID.randomUUID());
        networkParticipant.sendPacket(packet);
        CompletableFuture<Packet> future = new CompletableFuture<>();
        this.pendingQueries.put(packet.getQueryUUID(), future);
        return future;
    }

    /**
     * Sends a query and waits for the result up to 6 seconds
     *
     * @param networkParticipant the participant in the network, where the packet will be send to
     * @param packet the query-packet
     * @return the result-packet of the query or null
     */

    public Packet packetQuery(NetworkParticipant networkParticipant, Packet packet) {
        try {
            return this.packetQueryAsync(networkParticipant, packet).get(6, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CompletableFuture<Packet> getQueryAndRemove(UUID uuid) {
        return this.pendingQueries.remove(uuid);
    }


}
