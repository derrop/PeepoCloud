package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 07.11.2018
 */

import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;
import net.nevercloud.node.command.TabCompletable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
            NeverCloudNode.getInstance().getLanguagesManager().getAvailableLanguages(languages -> {
                StringBuilder builder = new StringBuilder();
                for (String language : languages) {
                    builder.append(language).append(", ");
                }
                sender.sendMessage(builder.substring(0, builder.length() - 2));
            });
        } else {
            NeverCloudNode.getInstance().getLanguagesManager().setSelectedLanguage(args[0], language -> {
                if (language != null) {
                    sender.sendMessage("&aSuccessfully changed language to &e" + language.getName() + " (" + language.getShortName() + ")");
                } else {
                    sender.sendMessage("&cThe specified language was not found on the server, using default language");
                }
            });
        }

    }

    @Override
    public String getUsage() {
        return "Sets the language of the System";
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String commandLine, String[] args) {
        Collection<String> collection = Arrays.asList("available");
        collection.addAll(NeverCloudNode.getInstance().getLanguagesManager().getAvailableLanguages());
        return collection;
    }
}
