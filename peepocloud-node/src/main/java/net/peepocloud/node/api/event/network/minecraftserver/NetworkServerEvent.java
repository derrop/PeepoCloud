package net.peepocloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.api.event.network.NetworkEvent;

@Getter
@AllArgsConstructor
/**
 * Events that are called when something with a started/starting server happens
 */
public class NetworkServerEvent extends NetworkEvent {
    private MinecraftServerInfo serverInfo;
}
