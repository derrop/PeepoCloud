package net.peepocloud.lib.pluginchannelmessage;

import net.peepocloud.lib.config.json.SimpleJsonObject;

public interface PluginChannelMessageManager {

    /**
     * Sends a pluginChannelMessage to the given serverTargetComponents
     *
     * @param identifier identifier to identify what the message is for
     * @param message the message
     * @param data extraData
     * @param targetComponents the components the pluginChannelMessage should be send to
     */

    void sendBukkitPluginChannelMessage(String identifier, String message, SimpleJsonObject data, String[] targetComponents);

    /**
     * Sends a pluginChannelMessage to all serverTargetComponents
     *
     * @param identifier identifier to identify what the message is for
     * @param message the message
     * @param data extraData
     */

    default void sendBukkitChannelMessage(String identifier, String message, SimpleJsonObject data) {
        this.sendBukkitPluginChannelMessage(identifier, message, data, null);
    }

    /**
     * Sends a pluginChannelMessage to the given bungeecordTargetComponents
     *
     * @param identifier identifier to identify what the message is for
     * @param message the message
     * @param data extraData
     * @param targetComponents the components the pluginChannelMessage should be send to
     */

    void sendBungeeCordPluginChannelMessage(String identifier, String message, SimpleJsonObject data, String[] targetComponents);

    /**
     * Sends a pluginChannelMessage to all bungeecordTargetComponents
     *
     * @param identifier identifier to identify what the message is for
     * @param message the message
     * @param data extraData
     */

    default void sendBungeeCordPluginChannelMessage(String identifier, String message, SimpleJsonObject data) {
        this.sendBungeeCordPluginChannelMessage(identifier, message, data, null);
    }

}
