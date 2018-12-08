package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import net.nevercloud.lib.server.bungee.BungeeGroup;
import net.nevercloud.lib.server.minecraft.MinecraftGroup;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;

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

        MinecraftGroup minecraftGroup = NeverCloudNode.getInstance().getMinecraftGroup(args[0]);
        if (minecraftGroup != null) {
            int started = 0;
            for (int i = 0; i < amount; i++) {
                if (NeverCloudNode.getInstance().startMinecraftServer(minecraftGroup) != null) {
                    started++;
                }
            }
            if (started == 1) {
                sender.createLanguageMessage("command.start.server.successful.one").replace("%group%", minecraftGroup.getName()).send();
            } else {
                sender.createLanguageMessage("command.start.server.successful.more").replace("%amount%", String.valueOf(started)).send();
            }
        } else {
            BungeeGroup bungeeGroup = NeverCloudNode.getInstance().getBungeeGroup(args[0]);
            if (bungeeGroup != null) {
                int started = 0;
                for (int i = 0; i < amount; i++) {
                    if (NeverCloudNode.getInstance().startBungeeProxy(bungeeGroup) != null) {
                        started++;
                    }
                }
                if (started == 1) {
                    sender.createLanguageMessage("command.start.proxy.successful.one").replace("%group%", bungeeGroup.getName()).send();
                } else {
                    sender.createLanguageMessage("command.start.proxy.successful.more").replace("%amount%", String.valueOf(started)).send();
                }
            } else {
                sender.createLanguageMessage("command.start.groupNotFound").replace("%group%", args[0]).send();
            }
        }
    }
}
