package net.peepocloud.plugin.network.packet.in.info;


import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class PacketInAPIServerInfos extends JsonPacket {
    private MinecraftServerInfo[] serverInfos;

    public PacketInAPIServerInfos() {
        super(104);
        this.serverInfos = super.getSimpleJsonObject().getObject("serverInfos", MinecraftServerInfo[].class);
    }

    public MinecraftServerInfo[] getServerInfos() {
        return serverInfos;
    }
}
