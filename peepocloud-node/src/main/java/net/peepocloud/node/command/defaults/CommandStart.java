package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import net.peepocloud.api.server.bungee.BungeeGroup;
import net.peepocloud.api.server.minecraft.MinecraftGroup;
import net.peepocloud.commons.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.command.Command;
import net.peepocloud.node.command.CommandSender;

public class CommandStart extends Command {
    public CommandStart() {
        super("start");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (args.length != 1 && args.length != 2) {
            sender.sendMessage("&estart <group> [amount]");
            return;
        }

        int amount = args.length == 2 && SystemUtils.isInteger(args[1]) ? Integer.parseInt(args[1]) : 1;
        if (amount < 1)
            amount = 1;

        MinecraftGroup minecraftGroup = PeepoCloudNode.getInstance().getMinecraftGroup(args[0]);
        if (minecraftGroup != null) {
            int started = 0;
            for (int i = 0; i < amount; i++) {
                if (PeepoCloudNode.getInstance().startMinecraftServer(minecraftGroup) != null) {
                    started++;
                }
            }
            if (started == 1) {
                sender.createLanguageMessage("command.start.server.successful.one").replace("%group%", minecraftGroup.getName()).send();
            } else {
                sender.createLanguageMessage("command.start.server.successful.more").replace("%group%", minecraftGroup.getName()).replace("%amount%", String.valueOf(started)).send();
            }
        } else {
            BungeeGroup bungeeGroup = PeepoCloudNode.getInstance().getBungeeGroup(args[0]);
            if (bungeeGroup != null) {
                int started = 0;
                for (int i = 0; i < amount; i++) {
                    if (PeepoCloudNode.getInstance().startBungeeProxy(bungeeGroup) != null) {
                        started++;
                    }
                }
                if (started == 1) {
                    sender.createLanguageMessage("command.start.proxy.successful.one").replace("%group%", bungeeGroup.getName()).send();
                } else {
                    sender.createLanguageMessage("command.start.proxy.successful.more").replace("%group%", bungeeGroup.getName()).replace("%amount%", String.valueOf(started)).send();
                }
            } else {
                sender.createLanguageMessage("command.start.groupNotFound").replace("%group%", args[0]).send();
            }
        }
    }
}
