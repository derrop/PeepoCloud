package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;

public class CommandDebug extends Command {
    public CommandDebug() {
        super("debug");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        PeepoCloudNode.getInstance().setDebugging(!PeepoCloudNode.getInstance().isDebugging());
        sender.sendMessageLanguageKey(PeepoCloudNode.getInstance().isDebugging() ? "command.debug.enabled" : "command.debug.disabled");
    }
}
