package net.peepocloud.node.network.packet.in.server;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.api.network.NetworkPacketSender;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.api.network.packet.JsonPacket;
import net.peepocloud.api.network.packet.Packet;
import net.peepocloud.api.network.packet.handler.JsonPacketHandler;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.server.process.BungeeProcess;
import net.peepocloud.node.server.process.CloudProcess;

import java.util.function.Consumer;

public class PacketInUpdateBungee extends JsonPacketHandler {
    @Override
    public void handlePacket(NetworkPacketSender networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        boolean a = false;
        BungeeCordProxyInfo serverInfo = packet.getSimpleJsonObject().getObject("proxyInfo", BungeeCordProxyInfo.class);
        if (PeepoCloudNode.getInstance().getProxiesOnThisNode().containsKey(serverInfo.getComponentName())) {
            PeepoCloudNode.getInstance().getProxiesOnThisNode().get(serverInfo.getComponentName()).setProxyInfo(serverInfo);
            a = true;
        }
        if (PeepoCloudNode.getInstance().getProcessManager().getProcesses().containsKey(serverInfo.getComponentName())) {
            CloudProcess process = PeepoCloudNode.getInstance().getProcessManager().getProcesses().get(serverInfo.getComponentName());
            if (process instanceof BungeeProcess) {
                ((BungeeProcess) process).setProxyInfo(serverInfo);
                a = true;
            }
        }
        if (a) {
            //TODO send api update packet to all components
        }
    }

    @Override
    public int getId() {
        return 16;
    }
}
