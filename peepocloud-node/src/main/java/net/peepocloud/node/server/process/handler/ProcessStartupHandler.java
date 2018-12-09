package net.peepocloud.node.server.process.handler;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import lombok.AllArgsConstructor;
import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.server.process.ProcessManager;

@AllArgsConstructor
public class ProcessStartupHandler implements Runnable {
    private ProcessManager processManager;
    @Override
    public void run() {
        while (!Thread.interrupted() && PeepoCloudNode.getInstance().isRunning()) {
            for (MinecraftGroup group : PeepoCloudNode.getInstance().getMinecraftGroups().values()) {
                if (
                        PeepoCloudNode.getInstance().getNextServerId(group.getName()) - 1 <
                                group.getMinServers()) {
                    PeepoCloudNode.getInstance().startMinecraftServer(group);
                }
            }
            for (BungeeGroup group : PeepoCloudNode.getInstance().getBungeeGroups().values()) {
                if (
                        PeepoCloudNode.getInstance().getNextProxyId(group.getName()) - 1 <
                                group.getMinServers()) {
                    PeepoCloudNode.getInstance().startBungeeProxy(group);
                }
            }
            SystemUtils.sleepUninterruptedly(500);
        }
    }
}
