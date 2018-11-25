package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;
import net.nevercloud.node.addon.defaults.DefaultAddonConfig;

public class CommandAddon extends Command {
    public CommandAddon() {
        super("addon");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            NeverCloudNode.getInstance().getDefaultAddonManager().getDefaultAddons(defaultAddonConfigs -> {
                if (defaultAddonConfigs == null) {
                    sender.sendMessageLanguageKey("command.addons.internalError");
                    return;
                }
                for (DefaultAddonConfig defaultAddonConfig : defaultAddonConfigs) {
                    StringBuilder authors = new StringBuilder();
                    for (String author : defaultAddonConfig.getAuthors()) {
                        authors.append(author).append(", ");
                    }
                    StringBuilder allVersions = new StringBuilder();
                    for (String version : defaultAddonConfig.getAllVersions()) {
                        allVersions.append(version).append(", ");
                    }
                    sender.createLanguageMessage("command.addons.defaultList0").replace("%name%", defaultAddonConfig.getName())
                            .replace("%authors%", authors.substring(0, authors.length() - 2)).send();
                    sender.createLanguageMessage("command.addons.defaultList1").replace("%version%", defaultAddonConfig.getVersion()).send();
                    sender.createLanguageMessage("command.addons.defaultList2").replace("%versions%", allVersions.substring(0, allVersions.length() - 2)).send();
                    sender.sendMessage(" ");
                }
            });
        } else if (args.length == 2 && args[0].equalsIgnoreCase("install")) {
            NeverCloudNode.getInstance().getDefaultAddonManager().getDefaultAddon(args[1], defaultAddonConfig -> {
                if (defaultAddonConfig == null) {
                    sender.sendMessageLanguageKey("command.addons.defaultNoAddonFound");
                    return;
                }

                sender.sendMessage(NeverCloudNode.getInstance().getDefaultAddonManager().installAddon(defaultAddonConfig).formatMessage(defaultAddonConfig));
            });
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            NeverCloudNode.getInstance().getDefaultAddonManager().getDefaultAddon(args[1], defaultAddonConfig -> {
                if (defaultAddonConfig == null) {
                    sender.sendMessageLanguageKey("command.addons.defaultNoAddonFound");
                    return;
                }

                if (NeverCloudNode.getInstance().getDefaultAddonManager().uninstallAddon(defaultAddonConfig)) {
                    sender.sendMessageLanguageKey("command.addons.defaultUninstalledSuccess");
                } else {
                    sender.sendMessageLanguageKey("command.addons.defaultNotInstalled");
                }
            });
        } else if (args.length == 2 && args[0].equalsIgnoreCase("update")) {
            NeverCloudNode.getInstance().getDefaultAddonManager().getDefaultAddon(args[1], defaultAddonConfig -> {
                if (defaultAddonConfig == null) {
                    sender.sendMessageLanguageKey("command.addons.defaultNoAddonFound");
                    return;
                }

                sender.sendMessage(NeverCloudNode.getInstance().getDefaultAddonManager().updateAddon(defaultAddonConfig).formatMessage(defaultAddonConfig));
            });
        } else {
            sender.sendMessage(
                    "addon list",
                    "addon install <name>",
                    "addon update <name>",
                    "addon remove <name>"
            );
        }
    }

}
