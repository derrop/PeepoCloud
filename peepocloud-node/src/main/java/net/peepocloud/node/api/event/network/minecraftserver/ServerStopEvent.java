package net.peepocloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.api.server.minecraft.MinecraftServerInfo;

@Getter
/**
 * Called when a server is stopped on this node instance
 */
public class ServerStopEvent extends NetworkServerEvent {
    public ServerStopEvent(MinecraftServerInfo serverInfo) {
        super(serverInfo);
    }
}
