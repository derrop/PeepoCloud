package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

public class CommandGStats extends Command {
    public CommandGStats() {
        super("gstats", null, "gstatistic", "gstatistics", "globalstatistic", "globalstatistics", "globalstats");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {

    }
}
