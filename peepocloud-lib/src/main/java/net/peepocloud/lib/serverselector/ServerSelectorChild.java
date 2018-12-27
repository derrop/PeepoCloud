package net.peepocloud.lib.serverselector;


import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class ServerSelectorChild {
    private String groupName;
    private transient MinecraftServerInfo serverInfo;

    public ServerSelectorChild(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setServerInfo(MinecraftServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public MinecraftServerInfo getServerInfo() {
        return serverInfo;
    }
}
