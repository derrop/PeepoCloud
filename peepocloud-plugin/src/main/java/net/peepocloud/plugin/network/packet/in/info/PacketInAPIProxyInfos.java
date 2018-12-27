package net.peepocloud.plugin.network.packet.in.info;

import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;

public class PacketInAPIProxyInfos extends JsonPacket {

    private BungeeCordProxyInfo[] proxyInfos;

    public PacketInAPIProxyInfos() {
        super(105);
        this.proxyInfos = super.getSimpleJsonObject().getObject("proxyInfos", BungeeCordProxyInfo[].class);
    }

    public BungeeCordProxyInfo[] getProxyInfos() {
        return proxyInfos;
    }
}
