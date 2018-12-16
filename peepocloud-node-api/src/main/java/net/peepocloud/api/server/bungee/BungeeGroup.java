package net.peepocloud.api.server.bungee;
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
public class BungeeGroup {
    private String name;
    private GroupMode groupMode;
    private List<Template> templates;
    private int memory;
    private int minServers;
    private int maxServers;
    private int startPort;

    public BungeeCordProxyInfo startBungeeProxy() {
        return PeepoAPI.getInstance().startBungeeProxy(this);
    }

    public BungeeCordProxyInfo startBungeeProxy(int memory) {
        return PeepoAPI.getInstance().startBungeeProxy(this, memory);
    }

    public BungeeCordProxyInfo startBungeeProxy(String name) {
        return PeepoAPI.getInstance().startBungeeProxy(this, name);
    }

    public BungeeCordProxyInfo startBungeeProxy(String name, int id, int memory) {
        return PeepoAPI.getInstance().startBungeeProxy(this, name, id, memory);
    }

    public BungeeCordProxyInfo startBungeeProxy(String name, int memory) {
        return PeepoAPI.getInstance().startBungeeProxy(this, name, memory);
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo) {
        return PeepoAPI.getInstance().startBungeeProxy(nodeInfo, this);
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, int memory) {
        return PeepoAPI.getInstance().startBungeeProxy(nodeInfo, this, memory);
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, String name) {
        return PeepoAPI.getInstance().startBungeeProxy(nodeInfo, this, name);
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, String name, int memory) {
        return PeepoAPI.getInstance().startBungeeProxy(nodeInfo, this, name, memory);
    }

    public BungeeCordProxyInfo startBungeeProxy(NodeInfo nodeInfo, String name, int id, int memory) {
        return PeepoAPI.getInstance().startBungeeProxy(nodeInfo, this, name, id, memory);
    }

    public void startBungeeProxy(BungeeCordProxyInfo proxyInfo) {
        PeepoAPI.getInstance().startBungeeProxy(proxyInfo);
    }
}
