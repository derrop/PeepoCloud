package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 06.11.2018
 */

import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

public class CommandReload extends Command {
    public CommandReload() {
        super("reload", null, "rl");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        NeverCloudNode.getInstance().reload();
    }
}
