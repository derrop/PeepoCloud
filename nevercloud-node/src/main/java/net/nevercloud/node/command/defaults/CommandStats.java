package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

public class CommandStats extends Command {
    public CommandStats() {
        super("stats",  null, "statistic", "statistics");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {

    }
}
