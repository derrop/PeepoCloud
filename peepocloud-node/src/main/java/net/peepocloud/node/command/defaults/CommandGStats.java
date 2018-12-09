package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import net.peepocloud.node.command.Command;
import net.peepocloud.node.command.CommandSender;

public class CommandGStats extends Command {
    public CommandGStats() {
        super("gstats", null, "gstatistic", "gstatistics", "globalstatistic", "globalstatistics", "globalstats");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {

    }
}
