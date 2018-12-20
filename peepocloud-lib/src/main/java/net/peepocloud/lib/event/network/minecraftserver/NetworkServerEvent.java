package net.peepocloud.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.api.event.network.NetworkEvent;
import net.peepocloud.api.server.minecraft.MinecraftServerInfo;

@Getter
@AllArgsConstructor
/**
 * Events that are called when something with a started/starting server happens
 */
public class NetworkServerEvent extends NetworkEvent {
    private MinecraftServerInfo serverInfo;
}
