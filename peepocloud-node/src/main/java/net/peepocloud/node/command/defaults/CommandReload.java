package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 06.11.2018
 */

import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;

public class CommandReload extends Command {
    public CommandReload() {
        super("reload", null, "rl");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        PeepoCloudNode.getInstance().getExecutorService().execute(() -> PeepoCloudNode.getInstance().reload());
    }
}
