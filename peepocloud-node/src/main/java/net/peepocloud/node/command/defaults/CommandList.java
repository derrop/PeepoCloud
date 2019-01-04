package net.peepocloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.server.minecraft.MinecraftState;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.command.Command;
import net.peepocloud.node.api.command.CommandSender;
import net.peepocloud.node.api.network.NodeParticipant;
import net.peepocloud.node.server.process.BungeeProcess;
import net.peepocloud.node.server.process.ProcessManager;
import net.peepocloud.node.server.process.ServerProcess;

import java.util.Collection;
import java.util.stream.Collectors;

public class CommandList extends Command {
    public CommandList() {
        super("list");
    }

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        sender.sendMessage("Memory on this node: " + PeepoCloudNode.getInstance().getMemoryUsedOnThisInstance() + "/" + PeepoCloudNode.getInstance().getCloudConfig().getMaxMemory() + " MB");
        sender.sendMessage("CPU on this node process: " + String.format("%.2f", SystemUtils.cpuUsageProcess()));
        sender.sendMessage("CPU on this system: " + String.format("%.2f", SystemUtils.cpuUsageSystem()));

        this.sendNodeInfo(sender, PeepoCloudNode.getInstance().getProcessManager());
        sender.sendMessage("Memory global: " + PeepoCloudNode.getInstance().getMemoryUsed() + "/" + PeepoCloudNode.getInstance().getMaxMemory() + " MB");

        if (!PeepoCloudNode.getInstance().getServerNodes().isEmpty()) {
            sender.sendMessage("Nodes:");
            for (NodeParticipant value : PeepoCloudNode.getInstance().getServerNodes().values()) {
                sender.sendMessage(" " + value.getName() + " @" + value.getAddress());
                if (value.getNodeInfo() != null) {
                    sender.sendMessage("  Memory: " + value.getNodeInfo().getUsedMemory() + "/" + value.getNodeInfo().getMaxMemory() + " MB");
                    sender.sendMessage("  CPU: " + value.getNodeInfo().getCpuUsage());
                }
                if (!value.getServers().isEmpty() || !value.getStartingServers().isEmpty() || !value.getWaitingServers().isEmpty()) {
                    sender.sendMessage("  Servers:");
                    if (!value.getServers().isEmpty()) {
                        sender.sendMessage("   Running:");
                        value.getServers().values().forEach(minecraftServerInfo -> this.server(sender, minecraftServerInfo));
                    }
                    if (!value.getStartingServers().isEmpty()) {
                        sender.sendMessage("   Starting:");
                        value.getStartingServers().values().forEach(minecraftServerInfo -> this.server(sender, minecraftServerInfo));
                    }
                    if (!value.getWaitingServers().isEmpty()) {
                        sender.sendMessage("   Queued:");
                        value.getWaitingServers().values().forEach(minecraftServerInfo -> this.server(sender, minecraftServerInfo));
                    }
                }

                if (!value.getProxies().isEmpty() || !value.getStartingProxies().isEmpty() || !value.getWaitingProxies().isEmpty()) {
                    sender.sendMessage("  Proxies:");
                    if (!value.getProxies().isEmpty()) {
                        sender.sendMessage("   Running:");
                        value.getProxies().values().forEach(minecraftServerInfo -> this.proxy(sender, minecraftServerInfo));
                    }
                    if (!value.getStartingProxies().isEmpty()) {
                        sender.sendMessage("   Starting:");
                        value.getStartingProxies().values().forEach(minecraftServerInfo -> this.proxy(sender, minecraftServerInfo));
                    }
                    if (!value.getWaitingProxies().isEmpty()) {
                        sender.sendMessage("   Queued:");
                        value.getWaitingProxies().values().forEach(minecraftServerInfo -> this.proxy(sender, minecraftServerInfo));
                    }
                }

            }
        }
    }

    private void sendNodeInfo(CommandSender sender, ProcessManager processManager) {
        Collection<ServerProcess> servers = processManager.getProcesses().values()
                .stream().filter(cloudProcess -> cloudProcess.isServer() && ((ServerProcess) cloudProcess).getServerInfo().getState() != MinecraftState.OFFLINE).map(cloudProcess -> (ServerProcess) cloudProcess).collect(Collectors.toList());
        Collection<BungeeProcess> proxies = processManager.getProcesses().values()
                .stream().filter(cloudProcess -> cloudProcess.isProxy() && cloudProcess.isRunning()).map(cloudProcess -> (BungeeProcess) cloudProcess).collect(Collectors.toList());

        Collection<ServerProcess> startingServers = processManager.getProcesses().values()
                .stream().filter(cloudProcess -> cloudProcess.isServer() && ((ServerProcess) cloudProcess).getServerInfo().getState() == MinecraftState.OFFLINE).map(cloudProcess -> (ServerProcess) cloudProcess).collect(Collectors.toList());
        Collection<BungeeProcess> startingProxies = processManager.getProcesses().values()
                .stream().filter(cloudProcess -> cloudProcess.isProxy() && !cloudProcess.isRunning()).map(cloudProcess -> (BungeeProcess) cloudProcess).collect(Collectors.toList());

        Collection<ServerProcess> waitingServers = processManager.getServerQueue().getServerProcesses()
                .stream().filter(cloudProcess -> cloudProcess.isServer()).map(cloudProcess -> (ServerProcess) cloudProcess).collect(Collectors.toList());
        Collection<BungeeProcess> waitingProxies = processManager.getServerQueue().getServerProcesses()
                .stream().filter(cloudProcess -> cloudProcess.isProxy()).map(cloudProcess -> (BungeeProcess) cloudProcess).collect(Collectors.toList());

        if (!servers.isEmpty() || !startingServers.isEmpty() || !waitingServers.isEmpty()) {
            sender.sendMessage("  Servers:");
            if (!servers.isEmpty()) {
                sender.sendMessage("   Running:");
                servers.forEach(serverProcess -> this.server(sender, serverProcess.getServerInfo()));
            }
            if (!startingServers.isEmpty()) {
                sender.sendMessage("   Starting:");
                startingServers.forEach(serverProcess -> this.server(sender, serverProcess.getServerInfo()));
            }
            if (!waitingServers.isEmpty()) {
                sender.sendMessage("   Queued:");
                servers.forEach(serverProcess -> this.server(sender, serverProcess.getServerInfo()));
            }
        }

        if (!proxies.isEmpty() || !startingProxies.isEmpty() || !waitingProxies.isEmpty()) {
            sender.sendMessage("  Proxies:");
            if (!proxies.isEmpty()) {
                sender.sendMessage("   Running:");
                proxies.forEach(cloudProcess -> this.proxy(sender, cloudProcess.getProxyInfo()));
            }
            if (!startingProxies.isEmpty()) {
                sender.sendMessage("   Starting:");
                proxies.forEach(cloudProcess -> this.proxy(sender, cloudProcess.getProxyInfo()));
            }
            if (!waitingProxies.isEmpty()) {
                sender.sendMessage("   Queued:");
                proxies.forEach(cloudProcess -> this.proxy(sender, cloudProcess.getProxyInfo()));
            }
        }
    }

    private void server(CommandSender sender, MinecraftServerInfo minecraftServerInfo) {
        sender.sendMessage("    - " + minecraftServerInfo.getComponentName() + " @" + minecraftServerInfo.getHost() + ":" + minecraftServerInfo.getPort() +
                " | Motd: " + minecraftServerInfo.getMotd() +
                " | State: " + minecraftServerInfo.getState().getName() +
                " | Template: " + minecraftServerInfo.getTemplate().getName() + "@" + minecraftServerInfo.getTemplate().getStorage() +
                " | Players: " + minecraftServerInfo.getPlayers().size() + "/" + minecraftServerInfo.getMaxPlayers());
    }

    private void proxy(CommandSender sender, BungeeCordProxyInfo proxyInfo) {
        sender.sendMessage("    - " + proxyInfo.getComponentName() + " @" + proxyInfo.getHost() + ":" + proxyInfo.getPort() +
                " | Template: " + proxyInfo.getTemplate().getName() + "@" + proxyInfo.getTemplate().getStorage() +
                " | Players: " + proxyInfo.getPlayers().size());
    }
}
