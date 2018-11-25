package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 10.11.2018
 */

import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

public class CommandUpdate extends Command {
    public CommandUpdate() {
        super("update");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        NeverCloudNode.getInstance().installUpdates(sender);
    }
}
