package net.peepocloud.plugin.bungee.event;

import net.md_5.bungee.api.plugin.Event;
import net.peepocloud.lib.config.json.SimpleJsonObject;

public class BungeeCordPluginChannelMessageEvent extends Event {
    private String senderComponent, identifier, message;
    private SimpleJsonObject data;

    public BungeeCordPluginChannelMessageEvent(String senderComponent, String identifier, String message, SimpleJsonObject data) {
        this.senderComponent = senderComponent;
        this.identifier = identifier;
        this.message = message;
        this.data = data;
    }

    public String getSenderComponent() {
        return senderComponent;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getMessage() {
        return message;
    }

    public SimpleJsonObject getData() {
        return data;
    }



}
