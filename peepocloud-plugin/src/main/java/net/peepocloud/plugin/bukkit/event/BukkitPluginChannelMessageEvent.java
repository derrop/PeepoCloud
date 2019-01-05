package net.peepocloud.plugin.bukkit.event;


import net.peepocloud.lib.config.json.SimpleJsonObject;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BukkitPluginChannelMessageEvent extends Event {
    private HandlerList handlerList = new HandlerList();

    private String senderComponent, identifier, message;
    private SimpleJsonObject data;

    public BukkitPluginChannelMessageEvent(String senderComponent, String identifier, String message, SimpleJsonObject data) {
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

    @Override
    public HandlerList getHandlers() {
        return this.handlerList;
    }
}
