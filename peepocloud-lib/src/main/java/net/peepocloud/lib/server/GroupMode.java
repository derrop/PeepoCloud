package net.peepocloud.lib.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import net.peepocloud.lib.server.bungee.BungeeGroup;
import net.peepocloud.lib.server.minecraft.MinecraftGroup;

import java.io.Serializable;

public enum GroupMode implements Serializable {
    /**
     * If SAVE is selected in a {@link MinecraftGroup}/{@link BungeeGroup}, it won't be deleted by the Node after a shutdown
     */
    SAVE,

    /**
     * If DELETE is selected in a {@link MinecraftGroup}/{@link BungeeGroup}, it will be deleted by the Node after a shutdown
     */
    DELETE
}
