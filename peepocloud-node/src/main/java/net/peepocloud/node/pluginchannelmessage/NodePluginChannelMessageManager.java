package net.peepocloud.node.pluginchannelmessage;

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.out.PacketOutPluginChannelMessage;
import net.peepocloud.lib.pluginchannelmessage.PluginChannelMessageManager;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.network.ClientNode;
import net.peepocloud.node.network.packet.out.PacketOutSendPacket;
import java.util.Arrays;

public class NodePluginChannelMessageManager implements PluginChannelMessageManager {


    public void sendBukkitPluginChannelMessage(String senderComponent, String identifier, String message, SimpleJsonObject data, String[] targetComponents) {
        PacketOutPluginChannelMessage packetOutPluginChannelMessage = new PacketOutPluginChannelMessage(
                senderComponent,
                NetworkComponentType.MINECRAFT_SERVER,
                identifier,
                message,
                data,
                targetComponents
        );
        if (targetComponents != null) {
            for (String targetComponent : targetComponents) {
                MinecraftServerInfo serverInfo = PeepoCloudNode.getInstance().getMinecraftServerInfo(targetComponent);
                if(serverInfo != null) {
                    ClientNode clientNode = PeepoCloudNode.getInstance().getConnectedNode(serverInfo.getParentComponentName());
                    if(clientNode != null)
                        clientNode.sendPacket(new PacketOutSendPacket(Arrays.asList(
                                new PacketOutSendPacket.PacketReceiver(NetworkComponentType.MINECRAFT_SERVER,
                                        serverInfo.getComponentName())), packetOutPluginChannelMessage));
                }
            }
        } else
            PeepoCloudNode.getInstance().getNetworkManager().sendPacketToServers(packetOutPluginChannelMessage);

    }

    @Override
    public void sendBukkitPluginChannelMessage(String identifier, String message, SimpleJsonObject data, String[] targetComponents) {
        this.sendBukkitPluginChannelMessage(PeepoCloudNode.getInstance().getNodeInfo().getName(), identifier, message, data, targetComponents);
    }


    public void sendBungeeCordPluginChannelMessage(String senderComponent, String identifier, String message, SimpleJsonObject data, String[] targetComponents) {
        PacketOutPluginChannelMessage packetOutPluginChannelMessage = new PacketOutPluginChannelMessage(
                senderComponent,
                NetworkComponentType.BUNGEECORD,
                identifier,
                message,
                data,
                targetComponents
        );
        if (targetComponents != null) {
            for (String targetComponent : targetComponents) {
                BungeeCordProxyInfo proxyInfo = PeepoCloudNode.getInstance().getBungeeProxyInfo(targetComponent);
                if(proxyInfo != null) {
                    ClientNode clientNode = PeepoCloudNode.getInstance().getConnectedNode(proxyInfo.getParentComponentName());
                    if(clientNode != null)
                        clientNode.sendPacket(new PacketOutSendPacket(Arrays.asList(
                                new PacketOutSendPacket.PacketReceiver(NetworkComponentType.BUNGEECORD,
                                        proxyInfo.getComponentName())), packetOutPluginChannelMessage));
                }

            }
        } else
            PeepoCloudNode.getInstance().getNetworkManager().sendPacketToProxies(packetOutPluginChannelMessage);

    }

    @Override
    public void sendBungeeCordPluginChannelMessage(String identifier, String message, SimpleJsonObject data, String[] targetComponents) {
        this.sendBukkitPluginChannelMessage(PeepoCloudNode.getInstance().getNodeInfo().getName(), identifier, message, data, targetComponents);
    }
}
