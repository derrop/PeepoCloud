package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 10.11.2018
 */

import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;

public class CommandVersion extends Command {
    public CommandVersion() {
        super("version");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        sender.createLanguageMessage("command.version.message").replace("%version%", SystemUtils.getCurrentVersion()).send();
    }
}
