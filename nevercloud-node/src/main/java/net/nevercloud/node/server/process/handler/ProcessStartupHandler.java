package net.nevercloud.node.server.process.handler;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import lombok.AllArgsConstructor;
import net.nevercloud.lib.server.bungee.BungeeGroup;
import net.nevercloud.lib.server.minecraft.MinecraftGroup;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.server.process.ProcessManager;

@AllArgsConstructor
public class ProcessStartupHandler implements Runnable {
    private ProcessManager processManager;
    @Override
    public void run() {
        while (!Thread.interrupted() && NeverCloudNode.getInstance().isRunning()) {
            for (MinecraftGroup group : NeverCloudNode.getInstance().getMinecraftGroups().values()) {
                if (
                        NeverCloudNode.getInstance().getNextServerId(group.getName()) - 1 <
                                group.getMinServers()) {
                    NeverCloudNode.getInstance().startMinecraftServer(group);
                }
            }
            for (BungeeGroup group : NeverCloudNode.getInstance().getBungeeGroups().values()) {
                if (
                        NeverCloudNode.getInstance().getNextProxyId(group.getName()) - 1 <
                                group.getMinServers()) {
                    NeverCloudNode.getInstance().startBungeeProxy(group);
                }
            }
            SystemUtils.sleepUninterruptedly(500);
        }
    }
}
