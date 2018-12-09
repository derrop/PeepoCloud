package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.command.Command;
import net.peepocloud.node.command.CommandSender;

public class CommandStop extends Command {
    public CommandStop() {
        super("stop", null, "exit", "end");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        PeepoCloudNode.getInstance().shutdown();
    }
}
