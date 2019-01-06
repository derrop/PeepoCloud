package net.peepocloud.plugin.network.packet.out.query;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;

import java.util.UUID;

public class PacketOutAPIQueryOnlinePlayers extends JsonPacket {


    private PacketOutAPIQueryOnlinePlayers(BungeeGroup bungeeGroup, MinecraftGroup minecraftGroup, UUID uniqueId, String name) {
        super(203);
        super.setSimpleJsonObject(new SimpleJsonObject().append("bungeeGroup", bungeeGroup)
                .append("minecraftGroup", minecraftGroup).append("uniqueId", uniqueId).append("name", name));
    }

    public PacketOutAPIQueryOnlinePlayers() {
        this(null,null, null, null);
    }

    public PacketOutAPIQueryOnlinePlayers(BungeeGroup group) {
        this(group,null, null, null);
    }

    public PacketOutAPIQueryOnlinePlayers(MinecraftGroup group) {
        this(null,group, null, null);
    }

    public PacketOutAPIQueryOnlinePlayers(UUID uniqueId) {
        this(null, null, uniqueId, null);
    }

    public PacketOutAPIQueryOnlinePlayers(String name) {
        this(null, null, null, name);
    }
}
