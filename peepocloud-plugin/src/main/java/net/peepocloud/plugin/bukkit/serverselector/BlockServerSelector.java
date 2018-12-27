package net.peepocloud.plugin.bukkit.serverselector;

import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.server.minecraft.MinecraftState;
import net.peepocloud.lib.serverselector.ServerSelector;
import net.peepocloud.lib.serverselector.ServerSelectorChild;
import net.peepocloud.plugin.api.network.handler.NetworkAPIHandlerAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class BlockServerSelector<Child extends ServerSelectorChild> extends NetworkAPIHandlerAdapter implements ServerSelector<Child> {
    protected List<Child> children = new ArrayList<>();
    protected Map<String, MinecraftServerInfo> waitingServers = new ConcurrentHashMap<>();


    public List<Child> freeChildren(String groupName) {
        return this.children.stream().filter(serverSign -> serverSign.getGroupName().equalsIgnoreCase(groupName)
                && (serverSign.getServerInfo() == null || serverSign.getServerInfo().getState() != MinecraftState.LOBBY)).collect(Collectors.toList());
    }

    @Override
    public void handleServerAdd(MinecraftServerInfo serverInfo) {
        List<Child> freeChildren = this.freeChildren(serverInfo.getGroupName());
        if(freeChildren.size() > 0) {
            this.waitingServers.remove(serverInfo.getComponentName().toLowerCase());
            Child freeChild = freeChildren.get(0);
            freeChild.setServerInfo(serverInfo);
            this.update(freeChild);
        } else if(!this.waitingServers.containsKey(serverInfo.getComponentName().toLowerCase()))
            this.waitingServers.put(serverInfo.getComponentName().toLowerCase(), serverInfo);
    }

    @Override
    public void handleServerStop(MinecraftServerInfo serverInfo) {
        for(Child child : this.children) {
            if(child.getServerInfo() != null && child.getServerInfo().getComponentName().equalsIgnoreCase(serverInfo.getComponentName()))
                child.setServerInfo(null);
        }
        for(MinecraftServerInfo current : this.waitingServers.values()) {
            if(current.getComponentName().equalsIgnoreCase(serverInfo.getComponentName()))
                this.waitingServers.remove(current.getComponentName().toLowerCase());
        }
    }
}
