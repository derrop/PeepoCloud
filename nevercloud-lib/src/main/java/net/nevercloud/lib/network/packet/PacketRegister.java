package net.nevercloud.lib.network.packet;


import java.util.HashMap;
import java.util.Map;

public class PacketRegister {

    private Map<Integer, Class<? extends Packet>> registeredPackets = new HashMap<>();

    public void registerPacket(Packet packet) {
        this.registeredPackets.put(packet.getId(), packet.getClass());
    }

    public Class<? extends Packet> getPacket(int id) {
        return this.registeredPackets.get(id);
    }


}
