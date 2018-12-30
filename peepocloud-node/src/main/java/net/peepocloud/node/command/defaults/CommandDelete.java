package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.server.Template;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;
import net.peepocloud.node.api.command.TabCompletable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandDelete extends Command implements TabCompletable {
    public CommandDelete() {
        super("delete", null, "del", "delet");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (args.length != 2 && args.length != 4) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("template")) {

            if (args[1].equalsIgnoreCase("minecraftgroup")) {

                if (!PeepoCloudNode.getInstance().getMinecraftGroups().containsKey(args[2])) {
                    sender.sendMessageLanguageKey("command.delete.minecraftgroup.notFound");
                    return;
                }

                String name = args[3];

                MinecraftGroup group = PeepoCloudNode.getInstance().getMinecraftGroup(args[2]);
                if (!deleteTemplate(sender, name, group.getTemplates())) {
                    return;
                }

                for (MinecraftServerInfo minecraftServer : PeepoCloudNode.getInstance().getMinecraftServers(group.getName())) {
                    if (minecraftServer.getTemplate().getName().equalsIgnoreCase(name)) {
                        minecraftServer.shutdown();
                    }
                }

                PeepoCloudNode.getInstance().updateMinecraftGroup(group);
                sender.createLanguageMessage("command.delete.template.success").replace("%template%", args[3]).send();

            } else if (args[1].equalsIgnoreCase("bungeegroup")) {

                if (!PeepoCloudNode.getInstance().getBungeeGroups().containsKey(args[2])) {
                    sender.sendMessageLanguageKey("command.delete.bungeegroup.notFound");
                    return;
                }

                String name = args[3];

                BungeeGroup group = PeepoCloudNode.getInstance().getBungeeGroup(args[2]);
                if (!deleteTemplate(sender, name, group.getTemplates())) {
                    return;
                }

                for (BungeeCordProxyInfo minecraftServer : PeepoCloudNode.getInstance().getBungeeProxies(group.getName())) {
                    if (minecraftServer.getTemplate().getName().equalsIgnoreCase(name)) {
                        minecraftServer.shutdown();
                    }
                }

                PeepoCloudNode.getInstance().updateBungeeGroup(group);
                sender.createLanguageMessage("command.delete.template.success").replace("%template%", args[3]).send();

            }

        } else if (args[0].equals("minecraftgroup")) {

            if (!PeepoCloudNode.getInstance().getMinecraftGroups().containsKey(args[1])) {
                sender.sendMessageLanguageKey("command.delete.minecraftgroup.notFound");
                return;
            }

            for (MinecraftServerInfo minecraftServer : PeepoCloudNode.getInstance().getMinecraftServers(args[1])) {
                minecraftServer.shutdown();
            }

            PeepoCloudNode.getInstance().deleteMinecraftGroup(args[1]);
            sender.sendMessageLanguageKey("command.delete.minecraftgroup.success");

        } else if (args[0].equalsIgnoreCase("bungeegroup")) {

            if (!PeepoCloudNode.getInstance().getBungeeGroups().containsKey(args[1])) {
                sender.sendMessageLanguageKey("command.delete.bungeegroup.notFound");
                return;
            }

            for (BungeeCordProxyInfo bungeeProxy : PeepoCloudNode.getInstance().getBungeeProxies(args[1])) {
                bungeeProxy.shutdown();
            }

            PeepoCloudNode.getInstance().deleteBungeeGroup(args[1]);
            sender.sendMessageLanguageKey("command.delete.bungeegroup.success");

        }

    }

    private boolean deleteTemplate(CommandSender sender, String name, List<Template> templates) {
        Template template = templates.stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst().orElse(null);

        if (template == null) {
            sender.sendMessageLanguageKey("command.delete.template.notFound");
            return false;
        }

        templates.remove(template);

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("&edelete <minecraftgroup|bungeegroup> <name>", "&edelete template <minecraftgroup|bungeegroup> <groupName> <name>");
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String commandLine, String[] args) {
        return args.length == 1 ? Arrays.asList("minecraftgroup", "bungeegroup", "template") :
                args.length == 2 ?
                        args[0].equalsIgnoreCase("minecraftgroup") ? PeepoCloudNode.getInstance().getMinecraftGroups().keySet() :
                                args[0].equalsIgnoreCase("bungeegroup") ? PeepoCloudNode.getInstance().getBungeeGroups().keySet() :
                                        args[0].equalsIgnoreCase("template") ? Arrays.asList("minecraftgroup", "bungeegroup") :
                                                Collections.emptyList()
                        : args.length == 3 ?
                        args[1].equalsIgnoreCase("minecraftgroup") ? PeepoCloudNode.getInstance().getMinecraftGroups().keySet() :
                                args[1].equalsIgnoreCase("bungeegroup") ? PeepoCloudNode.getInstance().getBungeeGroups().keySet() :
                                        Collections.emptyList() :
                        args.length == 4 ? args[1].equalsIgnoreCase("minecraftgroup") && PeepoCloudNode.getInstance().getMinecraftGroups().containsKey(args[2]) ?
                                PeepoCloudNode.getInstance().getMinecraftGroup(args[2]).getTemplates().stream().map(Template::getName).collect(Collectors.toList()) :
                                args[1].equalsIgnoreCase("bungeegroup") && PeepoCloudNode.getInstance().getBungeeGroups().containsKey(args[2]) ?
                                        PeepoCloudNode.getInstance().getBungeeGroup(args[2]).getTemplates().stream().map(Template::getName).collect(Collectors.toList()) :
                                        Collections.emptyList() : Collections.emptyList();
    }
}
