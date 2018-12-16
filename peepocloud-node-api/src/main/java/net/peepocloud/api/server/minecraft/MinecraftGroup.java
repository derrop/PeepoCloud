package net.peepocloud.api.server.minecraft;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.peepocloud.api.PeepoAPI;
import net.peepocloud.api.node.NodeInfo;
import net.peepocloud.api.server.GroupMode;
import net.peepocloud.api.server.Template;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MinecraftGroup {
    private String name;
    private GroupMode groupMode;
    private List<Template> templates;
    private int memory;
    private int minServers;
    private int maxServers;
    private int maxPlayers;
    private String motd;
    private int startPort;

    public MinecraftServerInfo startMinecraftServer() {
        return PeepoAPI.getInstance().startMinecraftServer(this);
    }

    public MinecraftServerInfo startMinecraftServer(int memory) {
        return PeepoAPI.getInstance().startMinecraftServer(this, memory);
    }

    public MinecraftServerInfo startMinecraftServer(String name) {
        return PeepoAPI.getInstance().startMinecraftServer(this, name);
    }

    public MinecraftServerInfo startMinecraftServer(String name, int id, int memory) {
        return PeepoAPI.getInstance().startMinecraftServer(this, name, id, memory);
    }

    public MinecraftServerInfo startMinecraftServer(String name, int memory) {
        return PeepoAPI.getInstance().startMinecraftServer(this, name, memory);
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo) {
        return PeepoAPI.getInstance().startMinecraftServer(nodeInfo, this);
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, int memory) {
        return PeepoAPI.getInstance().startMinecraftServer(nodeInfo, this, memory);
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, String name) {
        return PeepoAPI.getInstance().startMinecraftServer(nodeInfo, this, name);
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, String name, int memory) {
        return PeepoAPI.getInstance().startMinecraftServer(nodeInfo, this, name, memory);
    }

    public MinecraftServerInfo startMinecraftServer(NodeInfo nodeInfo, String name, int id, int memory) {
        return PeepoAPI.getInstance().startMinecraftServer(nodeInfo, this, name, id, memory);
    }

    public void startMinecraftServer(MinecraftServerInfo proxyInfo) {
        PeepoAPI.getInstance().startMinecraftServer(proxyInfo);
    }
}
