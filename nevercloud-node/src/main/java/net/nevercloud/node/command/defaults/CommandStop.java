package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

public class CommandStop extends Command {
    public CommandStop() {
        super("stop");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        NeverCloudNode.getInstance().shutdown();
    }

    @Override
    public String getUsage() {
        return "Stops the system";
    }
}
