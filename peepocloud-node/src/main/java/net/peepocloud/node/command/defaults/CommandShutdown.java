package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 30.12.2018
 */

import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;
import net.peepocloud.node.api.command.TabCompletable;
import net.peepocloud.node.api.network.NodeParticipant;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class CommandShutdown extends Command implements TabCompletable {
    public CommandShutdown() {
        super("shutdown", null, "stopserver");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("&eshutdown <group|server|bungee|node>");
            return;
        }

        String name = args[0];

        NodeParticipant node = PeepoCloudNode.getInstance().getServerNodes().get(name);
        if (node != null) {
            node.executeCommand("stop");
            sender.sendMessageLanguageKey("command.shutdown.node.success");
            return;
        }

        MinecraftGroup minecraftGroup = PeepoCloudNode.getInstance().getMinecraftGroup(name);
        if (minecraftGroup != null) {
            PeepoCloudNode.getInstance().stopMinecraftGroup(minecraftGroup);
            sender.sendMessageLanguageKey("command.shutdown.minecraftgroup.success");
            return;
        }

        BungeeGroup bungeeGroup = PeepoCloudNode.getInstance().getBungeeGroup(name);
        if (bungeeGroup != null) {
            PeepoCloudNode.getInstance().stopBungeeGroup(bungeeGroup);
            sender.sendMessageLanguageKey("command.shutdown.bungeegroup.success");
            return;
        }

        MinecraftServerInfo server = PeepoCloudNode.getInstance().getMinecraftServerInfo(name);
        if (server != null) {
            PeepoCloudNode.getInstance().stopMinecraftServer(server);
            sender.sendMessageLanguageKey("command.shutdown.server.success");
            return;
        }

        BungeeCordProxyInfo bungee = PeepoCloudNode.getInstance().getBungeeProxyInfo(name);
        if (bungee != null) {
            PeepoCloudNode.getInstance().stopBungeeProxy(bungee);
            sender.sendMessageLanguageKey("command.shutdown.bungee.success");
            return;
        }

        sender.sendMessageLanguageKey("command.shutdown.notFound");
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String commandLine, String[] args) {
        Collection<String> collection = new LinkedList<>();
        collection.addAll(PeepoCloudNode.getInstance().getServerNodes().keySet());
        collection.addAll(PeepoCloudNode.getInstance().getMinecraftServers().stream().map(MinecraftServerInfo::getComponentName).collect(Collectors.toList()));
        collection.addAll(PeepoCloudNode.getInstance().getBungeeProxies().stream().map(BungeeCordProxyInfo::getComponentName).collect(Collectors.toList()));
        collection.addAll(PeepoCloudNode.getInstance().getMinecraftGroups().keySet());
        collection.addAll(PeepoCloudNode.getInstance().getBungeeGroups().keySet());
        return collection;
    }
}
