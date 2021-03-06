package net.peepocloud.node.api.command;
/*
 * Created by Mc_Ruben on 25.11.2018
 */

import lombok.Data;
import net.peepocloud.node.api.PeepoCloudNodeAPI;

@Data
/**
 * Represents a message out of the language system which is addressed to the {@link CommandSender}
 */
public class LanguageMessage {

    private CommandSender sender;
    private String message;

    public LanguageMessage(String key) {
        this.message = PeepoCloudNodeAPI.getInstance().getLanguagesManager().getMessage(key);
    }

    LanguageMessage(CommandSender sender, String key) {
        this(key);
        this.sender = sender;
    }

    /**
     * Replaces a string in the message
     * @param regex The sequence of char values to be replaced
     * @param replacement The replacement sequence of char values
     * @see String#replace(CharSequence, CharSequence)
     * @return
     */
    public LanguageMessage replace(String regex, String replacement) {
        this.message = this.message.replace(regex, replacement);
        return this;
    }

    /**
     * Sends the message to the {@link CommandSender}
     */
    public void send() {
        this.sender.sendMessage(this.message);
    }

    @Override
    public String toString() {
        return this.message;
    }

}
