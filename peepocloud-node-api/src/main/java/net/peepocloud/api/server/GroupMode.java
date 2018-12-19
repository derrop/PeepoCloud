package net.peepocloud.api.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

public enum GroupMode {
    /**
     * If SAVE is selected in a {@link net.peepocloud.api.server.minecraft.MinecraftGroup}/{@link net.peepocloud.api.server.bungee.BungeeGroup}, it won't be deleted by the Node after a shutdown
     */
    SAVE,

    /**
     * If DELETE is selected in a {@link net.peepocloud.api.server.minecraft.MinecraftGroup}/{@link net.peepocloud.api.server.bungee.BungeeGroup}, it will be deleted by the Node after a shutdown
     */
    DELETE
}
