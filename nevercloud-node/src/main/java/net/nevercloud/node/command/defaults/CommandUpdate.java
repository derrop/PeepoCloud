package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 10.11.2018
 */

import io.netty.util.internal.PlatformDependent;
import lombok.*;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

public class CommandUpdate extends Command {
    public CommandUpdate() {
        super("update");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        NeverCloudNode.getInstance().installUpdates(sender);
    }
}
