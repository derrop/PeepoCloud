package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 04.12.2018
 */

import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;
import net.nevercloud.node.utility.NodeUtils;

public class CommandSupportUpdate extends Command {
    public CommandSupportUpdate() {
        super("supportupdate");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (!NeverCloudNode.getInstance().getCloudConfig().loadCredentials()) {
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
