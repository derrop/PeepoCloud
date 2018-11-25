package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

import java.util.Collection;
import java.util.HashSet;

public class CommandHelp extends Command {
    public CommandHelp() {
        super("help");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        Collection<String> sent = new HashSet<>();
        for (Command command : NeverCloudNode.getInstance().getCommandManager().getCommands().values()) {
            if (command.getClass().equals(CommandHelp.class))
                continue;
            if (sent.contains(command.getName()))
                continue;
            sent.add(command.getName());
            sender.sendMessage(command.getName());
            if (command.getPermission() != null) {
                sender.createLanguageMessage("command.help.permission").replace("%permission%", command.getPermission()).send();
            }
            if (command.getAliases() != null && command.getAliases().length != 0) {
                StringBuilder aliasesBuilder = new StringBuilder();
                for (String alias : command.getAliases()) {
                    aliasesBuilder.append(alias).append(", ");
                }
                sender.createLanguageMessage("command.help.aliases").replace("%aliases%", aliasesBuilder.substring(0, aliasesBuilder.length() - 2)).send();
            }
            String usage = command.getUsage();
            if (usage != null)
                sender.createLanguageMessage("command.help.usage").replace("%usage%", usage).send();
        }
    }
}
