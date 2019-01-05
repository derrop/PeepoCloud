package net.peepocloud.plugin.pluginchannelmessage;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.lib.network.packet.out.PacketOutPluginChannelMessage;
import net.peepocloud.lib.pluginchannelmessage.PluginChannelMessageManager;
import net.peepocloud.plugin.api.PeepoCloudPluginAPI;

public class PluginPluginChannelMessageManager implements PluginChannelMessageManager {
    private PeepoCloudPluginAPI pluginAPI;

    public PluginPluginChannelMessageManager(PeepoCloudPluginAPI pluginAPI) {
        this.pluginAPI = pluginAPI;
    }

    @Override
    public void sendBukkitPluginChannelMessage(String identifier, String message, SimpleJsonObject data, String[] targetComponents) {
        this.pluginAPI.getNodeConnector().sendPacket(new PacketOutPluginChannelMessage(
                this.pluginAPI.getComponentName(), NetworkComponentType.MINECRAFT_SERVER, identifier, message, data, targetComponents));
    }

    @Override
    public void sendBungeeCordPluginChannelMessage(String identifier, String message, SimpleJsonObject data, String[] targetComponents) {
        this.pluginAPI.getNodeConnector().sendPacket(new PacketOutPluginChannelMessage(
                this.pluginAPI.getComponentName(), NetworkComponentType.BUNGEECORD, identifier, message, data, targetComponents));
    }
}
