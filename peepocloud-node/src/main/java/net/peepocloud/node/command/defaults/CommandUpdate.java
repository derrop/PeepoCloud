package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 10.11.2018
 */

import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.command.Command;
import net.peepocloud.node.command.CommandSender;

public class CommandUpdate extends Command {
    public CommandUpdate() {
        super("update");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        PeepoCloudNode.getInstance().installUpdates(sender);
    }
}
