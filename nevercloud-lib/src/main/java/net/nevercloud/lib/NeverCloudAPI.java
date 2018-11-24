package net.nevercloud.lib;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.node.NodeInfo;
import net.nevercloud.lib.server.BungeeGroup;
import net.nevercloud.lib.server.MinecraftGroup;
import net.nevercloud.lib.server.MinecraftServerInfo;

public interface NeverCloudAPI {

    BungeeGroup getBungeeGroup(String name);

    MinecraftGroup getMinecraftGroup(String name);

    void sendPacketToNodes(Packet packet);

    void updateMinecraftGroup(MinecraftGroup group);

    void updateBungeeGroup(BungeeGroup group);
    
    MinecraftServerInfo startMinecraftServer(MinecraftGroup group);

    MinecraftServerInfo startMinecraftServer(MinecraftGroup group, int memory);

    MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name);

    MinecraftServerInfo startMinecraftServer(MinecraftGroup group, String name, int memory);

    MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group);

    MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, int memory);

    MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name);

    MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, MinecraftGroup group, String name, int memory);
    
}
