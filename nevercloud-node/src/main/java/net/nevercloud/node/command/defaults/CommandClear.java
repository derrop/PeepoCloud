package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 25.11.2018
 */

import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

public class CommandClear extends Command {
    public CommandClear() {
        super("clear");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        NeverCloudNode.getInstance().getLogger().clearScreen();
    }
}
