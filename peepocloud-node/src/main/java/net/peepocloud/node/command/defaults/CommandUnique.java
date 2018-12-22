package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 04.12.2018
 */

import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;

public class CommandUnique extends Command {
    public CommandUnique() {
        super("unique");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (PeepoCloudNode.getInstance().getUniqueId() == null)
            PeepoCloudNode.getInstance().getCloudConfig().loadCredentials();
        sender.sendMessage("&aThe unique id of your cloud is: &e" + (PeepoCloudNode.getInstance().getUniqueId() == null ? "not available" : PeepoCloudNode.getInstance().getUniqueId()));
    }
}
