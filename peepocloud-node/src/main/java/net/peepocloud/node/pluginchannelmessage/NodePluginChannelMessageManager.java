package net.peepocloud.node.pluginchannelmessage;

import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.out.PacketOutPluginChannelMessage;
import net.peepocloud.lib.pluginchannelmessage.PluginChannelMessageManager;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.PeepoCloudNode;

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
                if(serverInfo != null)
                    PeepoCloudNode.getInstance().getNetworkManager().sendPacketToServer(serverInfo, packetOutPluginChannelMessage);
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
                if(proxyInfo != null)
                    PeepoCloudNode.getInstance().getNetworkManager().sendPacketToProxy(proxyInfo, packetOutPluginChannelMessage);
            }
        } else
            PeepoCloudNode.getInstance().getNetworkManager().sendPacketToProxies(packetOutPluginChannelMessage);

    }

    @Override
    public void sendBungeeCordPluginChannelMessage(String identifier, String message, SimpleJsonObject data, String[] targetComponents) {
        this.sendBukkitPluginChannelMessage(PeepoCloudNode.getInstance().getNodeInfo().getName(), identifier, message, data, targetComponents);
    }
}
