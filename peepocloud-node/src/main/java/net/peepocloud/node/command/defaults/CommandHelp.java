package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CommandHelp extends Command {
    public CommandHelp() {
        super("help");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        for (Command command : PeepoCloudNode.getInstance().getCommandManager().getCommands().values()) {
            if (command.getClass().equals(CommandHelp.class))
                continue;
            sender.sendMessage(command.getName());
            if (command.getPermission() != null) {
                sender.createLanguageMessage("command.help.permission").replace("%permission%", command.getPermission()).send();
            }
            if (command.getAliases() != null && command.getAliases().length != 0) {
                sender.createLanguageMessage("command.help.aliases").replace("%aliases%", Arrays.stream(command.getAliases()).collect(Collectors.joining(", "))).send();
            }
            String usage = command.getUsage();
            if (usage != null)
                sender.createLanguageMessage("command.help.usage").replace("%usage%", usage).send();
        }
    }
}
