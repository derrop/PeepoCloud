package net.nevercloud.node.command;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import net.nevercloud.node.NeverCloudNode;

public interface CommandSender {

    default void sendMessage(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    String getName();

    void sendMessage(String message);

    default void sendMessageLanguageKey(String key) {
        this.sendMessage(NeverCloudNode.getInstance().getLanguagesManager().getMessage(key));
    }

    default void sendMessageLanguageKey(String... keys) {
        for (String key : keys) {
            this.sendMessageLanguageKey(key);
        }
    }

    default LanguageMessage createLanguageMessage(String key) {
        return new LanguageMessage(this, key);
    }

    boolean hasPermission(String permission);

}
