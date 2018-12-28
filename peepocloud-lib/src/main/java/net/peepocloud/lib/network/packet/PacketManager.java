package net.peepocloud.lib.network.packet;


import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.utility.network.QueryRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PacketManager {
    private Map<Integer, PacketInfo> registeredPackets = new HashMap<>();
    private Map<UUID, QueryRequest<Packet>> pendingQueries = new HashMap<>();
    private Map<Class<? extends Packet>, Integer> queryResponses = new HashMap<>();

    {
        queryResponses.put(FilePacket.class, -1);
        queryResponses.put(JsonPacket.class, -2);
        queryResponses.put(SerializationPacket.class, -3);
    }

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

    public Map<Class<? extends Packet>, Integer> getQueryResponses() {
        return queryResponses;
    }

    public void registerQueryResponsePacket(int id, Class<? extends Packet> packetClass) {
        this.queryResponses.put(packetClass, id);
    }

    public Packet convertToQueryPacket(Packet packet, UUID uuid) {
        packet.setQueryUUID(uuid);
        return packet;
    }

    /**
     * Sends a query and completes the future when the value is available
     *
     * @param networkParticipant the participant in the network, where the packet will be sent to
     * @param packet the query-packet
     * @return future for the packet
     */

    public QueryRequest<Packet> packetQueryAsync(NetworkPacketSender networkParticipant, Packet packet) {
        this.convertToQueryPacket(packet, UUID.randomUUID());
        networkParticipant.sendPacket(packet);
        QueryRequest<Packet> request = new QueryRequest<>();
        this.pendingQueries.put(packet.getQueryUUID(), request);
        return request;
    }

    /**
     * Sends a query and waits for the result up to 6 seconds
     *
     * @param networkParticipant the participant in the network, where the packet will be sent to
     * @param packet the query-packet
     * @return the result-packet of the query or null
     */

    public Packet packetQuery(NetworkPacketSender networkParticipant, Packet packet) {
        return this.packetQueryAsync(networkParticipant, packet).complete(6, TimeUnit.SECONDS);
    }

    public QueryRequest<Packet> getQueryAndRemove(UUID uuid) {
        return this.pendingQueries.remove(uuid);
    }


}
