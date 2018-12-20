package net.peepocloud.node.server.process.handler;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;

public class ProcessStartupHandler implements Runnable {
    @Override
    public void run() {
        while (!Thread.interrupted() && PeepoCloudNode.getInstance().isRunning()) {
            if (!PeepoCloudNode.getInstance().getNetworkServer().isSelfNodeCore()) {
                SystemUtils.sleepUninterruptedly(2000);
                continue;
            }

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
