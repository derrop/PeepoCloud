package net.nevercloud.lib.network.packet;


import java.util.HashMap;
import java.util.Map;

public class PacketRegister {

    private Map<Integer, Class<? extends Packet>> registeredPackets = new HashMap<>();

    public void registerPacket(int id, Class<? extends Packet> packetClass) {
        this.registeredPackets.put(id, packetClass);
    }

    public Class<? extends Packet> getPacket(int id) {
        return this.registeredPackets.get(id);
    }


}
