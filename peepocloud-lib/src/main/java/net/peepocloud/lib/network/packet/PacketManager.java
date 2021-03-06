package net.peepocloud.lib.network.packet;


import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.lib.network.packet.serialization.SerializationPacket;
import net.peepocloud.lib.utility.network.FunctionalQueryRequest;
import net.peepocloud.lib.utility.network.QueryRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

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


    public Packet convertToQueryResponse(Packet packet, UUID uuid) {
        packet.setQueryUUID(uuid);
        Class<? extends Packet> clazz = packet.getClass();
        if (!this.queryResponses.containsKey(clazz)) {
            if (packet instanceof JsonPacket) {
                clazz = JsonPacket.class;
            } else if (packet instanceof SerializationPacket) {
                clazz = SerializationPacket.class;
            } else if (packet instanceof FilePacket) {
                clazz = FilePacket.class;
            }
        }
        packet.setId(this.queryResponses.get(clazz));
        return packet;
    }

    public boolean hasQuery(UUID uuid) {
        return this.pendingQueries.containsKey(uuid);
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
        QueryRequest<Packet> request = new QueryRequest<>();
        this.pendingQueries.put(packet.getQueryUUID(), request);
        networkParticipant.sendPacket(packet);
        return request;
    }

    /**
     * Sends a query and completes the future when the value is available with the result of the {@link Function}
     *
     * @param networkParticipant the participant in the network, where the packet will be sent to
     * @param packet the query-packet
     * @param function the function where the packet will be applied to
     * @return future for the result of the function
     */
    public <T> QueryRequest<T> packetQueryAsync(NetworkPacketSender networkParticipant, Packet packet, Function<Packet, T> function) {
        FunctionalQueryRequest<T> request = new FunctionalQueryRequest<>(function);
        this.packetQueryAsync(networkParticipant, packet).onComplete(request::setResponse);
        return request;
    }

    /**
     * Sends a query and waits for the result up to 4 seconds
     *
     * @param networkParticipant the participant in the network, where the packet will be sent to
     * @param packet the query-packet
     * @return the result-packet of the query or null
     */

    public Packet packetQuery(NetworkPacketSender networkParticipant, Packet packet) {
        return this.packetQueryAsync(networkParticipant, packet).complete(4, TimeUnit.SECONDS);
    }

    /**
     * Sends a query and waits for the result up to 4 seconds and applies the result to the {@link Function}
     *
     * @param networkParticipant the participant in the network, where the packet will be sent to
     * @param packet the query-packet
     * @param function the function where the packet will be applied to
     * @return the result of the query or null
     */

    public <T> T packetQuery(NetworkPacketSender networkParticipant, Packet packet, Function<Packet, T> function) {
        return function.apply(this.packetQuery(networkParticipant, packet));
    }

    public QueryRequest<Packet> getQueryAndRemove(UUID uuid) {
        return this.pendingQueries.remove(uuid);
    }


}
