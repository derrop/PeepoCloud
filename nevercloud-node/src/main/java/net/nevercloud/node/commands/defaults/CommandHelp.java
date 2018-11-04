package net.nevercloud.node.commands.defaults;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.commands.Command;
import net.nevercloud.node.commands.CommandSender;

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
            if (sent.contains(command.getName()))
                continue;
            sent.add(command.getName());
            StringBuilder builder = new StringBuilder();
            builder.append(command.getName());
            if (command.getPermission() != null) {
                builder.append("\n - Permission: ").append(command.getPermission());
            }
            if (command.getAliases() != null && command.getAliases().length != 0) {
                StringBuilder aliasesBuilder = new StringBuilder();
                for (String alias : command.getAliases()) {
                    aliasesBuilder.append(alias).append(", ");
                }
                builder.append("\n - Aliases: ").append(aliasesBuilder.substring(0, aliasesBuilder.length() - 2));
            }
            sender.sendMessage(builder.toString());
        }
    }
}
