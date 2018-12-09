package net.peepocloud.node.command;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import net.peepocloud.node.PeepoCloudNode;

/**
 * Represents a CommandSender to which we can send messages
 */
public interface CommandSender {

    /**
     * Sends the messages to this CommandSender
     * @param messages the messages
     */
    default void sendMessage(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    /**
     * Gets the name of this CommandSender
     * @return the name of this CommandSender
     */
    String getName();

    /**
     * Sends one message to this CommandSender
     * @param message the messages
     */
    void sendMessage(String message);

    /**
     * Sends a message out of the language system by the specified key
     * @param key the key of the message in the language system
     */
    default void sendMessageLanguageKey(String key) {
        this.sendMessage(PeepoCloudNode.getInstance().getLanguagesManager().getMessage(key));
    }

    /**
     * Sends messages out of the language system by the specified keys
     * @param keys the keys of the messages in the language system
     */
    default void sendMessageLanguageKey(String... keys) {
        for (String key : keys) {
            this.sendMessageLanguageKey(key);
        }
    }

    /**
     * Creates a language message sending to this CommandSender to easily get a message out of the language system and replace some strings in it
     * @param key the key of the message in the language system
     * @return a new {@link LanguageMessage} to this CommandSender with the message with the specified key out of the language system
     */
    default LanguageMessage createLanguageMessage(String key) {
        return new LanguageMessage(this, key);
    }

    /**
     * Checks if this CommandSender has a permission
     * @param permission the permission to check for
     * @return {@code true} if this CommandSender has the permission or {@code false} if not
     */
    boolean hasPermission(String permission);

}
