package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 07.11.2018
 */

import lombok.*;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;
import net.nevercloud.node.languagesystem.Language;

public class CommandLanguage extends Command {
    public CommandLanguage() {
        super("language", null, "lang");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("language <name>");
            return;
        }

        NeverCloudNode.getInstance().getLanguagesManager().setSelectedLanguage(args[0], language -> {
            if (language != null) {
                sender.sendMessage("&aSuccessfully changed language to &e" + language.getName() + " (" + language.getShortName() + ")");
            } else {
                sender.sendMessage("&cThe specified language was not found on the server, using default language");
            }
        });
    }

    @Override
    public String getUsage() {
        return "Sets the language of the System";
    }
}
