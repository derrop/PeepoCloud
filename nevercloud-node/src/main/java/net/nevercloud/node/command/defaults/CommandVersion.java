package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 10.11.2018
 */

import lombok.*;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

public class CommandVersion extends Command {
    public CommandVersion() {
        super("version");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        sender.createLanguageMessage("command.version.message").replace("%version%", SystemUtils.getCurrentVersion()).send();
    }
}
