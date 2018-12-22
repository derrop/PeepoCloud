package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 05.12.2018
 */

import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;

public class CommandStats extends Command {
    public CommandStats() {
        super("stats",  null, "statistic", "statistics");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {

    }
}
