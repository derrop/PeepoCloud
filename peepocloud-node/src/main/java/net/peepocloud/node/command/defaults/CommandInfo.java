package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 04.01.2019
 */

import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;
import net.peepocloud.node.api.command.TabCompletable;
import oshi.software.os.OSProcess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class CommandInfo extends Command implements TabCompletable {
    public CommandInfo() {
        super("info", null, "i");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("&einfo <server|bungee>");
            return;
        }

        MinecraftServerInfo serverInfo = PeepoCloudNode.getInstance().getMinecraftServerInfo(args[0]);
        if (serverInfo != null && serverInfo.getPid() != -1) {
            PeepoCloudNode.getInstance().getProcessOfServerInfo(serverInfo).onComplete(process -> this.sendInfo(sender, serverInfo.getComponentName(), process, serverInfo.getMemory()));
            return;
        }

        BungeeCordProxyInfo proxyInfo = PeepoCloudNode.getInstance().getBungeeProxyInfo(args[0]);
        if (proxyInfo != null && proxyInfo.getPid() != -1) {
            PeepoCloudNode.getInstance().getProcessOfProxyInfo(proxyInfo).onComplete(process -> this.sendInfo(sender, proxyInfo.getComponentName(), process, proxyInfo.getMemory()));
            return;
        }

        sender.sendMessageLanguageKey("command.info.notFound");
    }

    private void sendInfo(CommandSender sender, String name, OSProcess process, int maxMemory) {
        if (process == null) {
            sender.sendMessageLanguageKey("command.info.notFound");
            return;
        }
        sender.sendMessage("Info for " + name + ":");
        sender.sendMessage(" ThreadCount: " + process.getThreadCount());
        sender.sendMessage(" CPU Usage: " + String.format("%.2f", (double)(process.getKernelTime() + process.getUserTime()) / (double)process.getUpTime()) + " %");
        sender.sendMessage(" Memory Usage: " + (process.getVirtualSize() / 1024D / 1024D) + " MB / " + maxMemory + " MB");
        sender.sendMessage(" StartTime: " + SystemUtils.DEFAULT_DATE_FORMAT.format(new Date(process.getStartTime())));
        sender.sendMessage(" Files open: " + process.getOpenFiles());
        sender.sendMessage(" ProcessId: " + process.getProcessID());
        sender.sendMessage(" UpTime: " + String.format("%.2f", process.getUpTime() / 1000D / 60D) + " minutes");
        sender.sendMessage(" State: " + process.getState().name().toLowerCase());
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String commandLine, String[] args) {
        Collection<String> collection = new ArrayList<>();
        collection.addAll(PeepoCloudNode.getInstance().getMinecraftServers().stream().filter(serverInfo -> serverInfo.getPid() != -1).map(MinecraftServerInfo::getComponentName).collect(Collectors.toList()));
        collection.addAll(PeepoCloudNode.getInstance().getBungeeProxies().stream().filter(proxyInfo -> proxyInfo.getPid() != -1).map(BungeeCordProxyInfo::getComponentName).collect(Collectors.toList()));
        return collection;
    }
}
