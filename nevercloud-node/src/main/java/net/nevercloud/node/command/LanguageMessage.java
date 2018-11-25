package net.nevercloud.node.command;
/*
 * Created by Mc_Ruben on 25.11.2018
 */

import lombok.*;
import net.nevercloud.node.NeverCloudNode;

@Data
public class LanguageMessage {

    private CommandSender sender;
    private String message;

    public LanguageMessage(String key) {
        this.message = NeverCloudNode.getInstance().getLanguagesManager().getMessage(key);
    }

    LanguageMessage(CommandSender sender, String key) {
        this(key);
        this.sender = sender;
    }

    public LanguageMessage replace(String regex, String replacement) {
        this.message = this.message.replace(regex, replacement);
        return this;
    }

    public void send() {
        this.sender.sendMessage(this.message);
    }

    @Override
    public String toString() {
        return this.message;
    }

}
