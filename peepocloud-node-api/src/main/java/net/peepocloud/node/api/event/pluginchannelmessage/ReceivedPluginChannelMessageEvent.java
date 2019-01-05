package net.peepocloud.node.api.event.pluginchannelmessage;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.auth.NetworkComponentType;
import net.peepocloud.node.api.event.CancellableEvent;

/**
 * Event which is being called when the current node receives a pluginMessage which will be send to another server/bungeecord.
 * When cancelled, it's not being send to them.
 */

public class ReceivedPluginChannelMessageEvent extends CancellableEvent {
    private String senderComponent;
    private NetworkComponentType targetType;
    private String identifier, message;
    private SimpleJsonObject data;
    private String[] targetComponents;

    public ReceivedPluginChannelMessageEvent(String senderComponent, NetworkComponentType targetType, String identifier, String message, SimpleJsonObject data, String[] targetComponents) {
        this.senderComponent = senderComponent;
        this.targetType = targetType;
        this.identifier = identifier;
        this.message = message;
        this.data = data;
        this.targetComponents = targetComponents;
    }

    public String getSenderComponent() {
        return senderComponent;
    }

    public NetworkComponentType getTargetType() {
        return targetType;
    }

    public void setTargetType(NetworkComponentType targetType) {
        this.targetType = targetType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SimpleJsonObject getData() {
        return data;
    }

    public void setData(SimpleJsonObject data) {
        this.data = data;
    }

    public String[] getTargetComponents() {
        return targetComponents;
    }

    public void setTargetComponents(String[] targetComponents) {
        this.targetComponents = targetComponents;
    }
}
