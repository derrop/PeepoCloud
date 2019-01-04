package net.peepocloud.plugin.bukkit.command;

import net.peepocloud.plugin.bukkit.command.subcommand.SubCommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CloudPluginCommand implements CommandExecutor {
    private Map<String, SubCommandExecutor> subCommandExecutors = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("peepocloud.command.cloudplugin"))
            return false;
        if(args.length == 0) {
            this.sendHelp(sender);
        } else {
            String name = args[0].toLowerCase();
            SubCommandExecutor subCommandExecutor = this.subCommandExecutors.get(name);
            if(subCommandExecutor != null)
                return subCommandExecutor.subExecutor().onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            else
                this.sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§7/cloudplugin - Help");
        for(SubCommandExecutor subCommandExecutor : this.subCommandExecutors.values())
            sender.sendMessage("  §e/cloudplugin " + subCommandExecutor.getUsage() + " §8> §7" + subCommandExecutor.getDescription());

    }

    public void registerSubCommand(SubCommandExecutor subCommandExecutor) {
        this.subCommandExecutors.put(subCommandExecutor.getName().toLowerCase(), subCommandExecutor);
    }
}
