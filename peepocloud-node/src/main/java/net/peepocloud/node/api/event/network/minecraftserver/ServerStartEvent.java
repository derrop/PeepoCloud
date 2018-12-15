package net.peepocloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;

@Getter
/**
 * Called when a server is started on one node in the network
 */
public class ServerStartEvent extends NetworkServerEvent {
    public ServerStartEvent(MinecraftServerInfo serverInfo) {
        super(serverInfo);
    }
}
