package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 04.12.2018
 */

import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;
import net.peepocloud.node.utility.NodeUtils;

public class CommandSupportUpdate extends Command {
    public CommandSupportUpdate() {
        super("supportupdate");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (!PeepoCloudNode.getInstance().getCloudConfig().loadCredentials()) {
            sender.sendMessageLanguageKey("support.infoUpdate.failure");
            return;
        }
        NodeUtils.updateNodeInfoForSupport(success -> {
            if (success == null) {
                sender.sendMessageLanguageKey("support.infoUpdate.disabled");
            } else if (success) {
                sender.sendMessageLanguageKey("support.infoUpdate.success");
            } else {
                sender.sendMessageLanguageKey("support.infoUpdate.failure");
            }
        });
    }
}
