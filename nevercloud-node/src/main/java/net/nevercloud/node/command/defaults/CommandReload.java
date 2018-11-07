package net.nevercloud.node.commands.defaults;
/*
 * Created by Mc_Ruben on 06.11.2018
 */

import lombok.*;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.commands.Command;
import net.nevercloud.node.commands.CommandSender;

public class CommandReload extends Command {
    public CommandReload() {
        super("reload", null, "rl");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        NeverCloudNode.getInstance().reload();
    }

    @Override
    public String getUsage() {
        return "Reloads the system (the addons, configs)";
    }
}
