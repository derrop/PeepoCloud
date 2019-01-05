package net.peepocloud.lib.serverselector;


import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

public class ServerSelectorChild {
    private String groupName;
    private String savedOnGroup;
    private transient MinecraftServerInfo serverInfo;

    public ServerSelectorChild(String groupName, String savedOnGroup) {
        this.groupName = groupName;
        this.savedOnGroup = savedOnGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSavedOnGroup() {
        return savedOnGroup;
    }

    public void setServerInfo(MinecraftServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public MinecraftServerInfo getServerInfo() {
        return serverInfo;
    }
}
