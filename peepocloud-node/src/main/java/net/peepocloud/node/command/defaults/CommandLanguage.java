package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 07.11.2018
 */

import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;
import net.peepocloud.node.api.command.TabCompletable;

import java.util.ArrayList;
import java.util.Collection;

public class CommandLanguage extends Command implements TabCompletable {
    public CommandLanguage() {
        super("language", null, "lang");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("language <name>", "language available");
            return;
        }

        if (args[0].equalsIgnoreCase("available")) {
            PeepoCloudNode.getInstance().getLanguagesManager().getAvailableLanguages(languages -> {
                StringBuilder builder = new StringBuilder();
                for (String language : languages) {
                    builder.append(language).append(", ");
                }
                sender.sendMessage(builder.substring(0, builder.length() - 2));
            });
        } else {
            PeepoCloudNode.getInstance().getLanguagesManager().setSelectedLanguage(args[0], language -> {
                if (language != null) {
                    sender.sendMessage("&aSuccessfully changed language to &e" + language.getName() + " (" + language.getShortName() + ")");
                } else {
                    sender.sendMessage("&cThe specified language was not found on the process, using default language");
                }
            });
        }

    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String commandLine, String[] args) {
        Collection<String> collection = new ArrayList<>();
        collection.add("available");
        collection.addAll(PeepoCloudNode.getInstance().getLanguagesManager().getAvailableLanguages());
        return collection;
    }
}
