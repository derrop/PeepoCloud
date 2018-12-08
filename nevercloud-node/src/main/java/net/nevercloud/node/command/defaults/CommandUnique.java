package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 04.12.2018
 */

import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

public class CommandUnique extends Command {
    public CommandUnique() {
        super("unique");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (NeverCloudNode.getInstance().getUniqueId() == null)
            NeverCloudNode.getInstance().getCloudConfig().loadCredentials();
        sender.sendMessage("&aThe unique id of your cloud is: &e" + (NeverCloudNode.getInstance().getUniqueId() == null ? "not available" : NeverCloudNode.getInstance().getUniqueId()));
    }
}
