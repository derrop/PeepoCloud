package net.peepocloud.addons.templates.ftp.command;
/*
 * Created by Mc_Ruben on 06.01.2019
 */

import net.peepocloud.addons.templates.ftp.FtpTemplatesAddon;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;

public class CommandFtpClearCache extends Command {
    public CommandFtpClearCache() {
        super("ftp-clearcache", null, "ftp-cc", "ftpcc");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        FtpTemplatesAddon.getInstance().getTemplateStorage().clearCache();
        sender.sendMessage("The ftp cache was cleared");
    }
}
