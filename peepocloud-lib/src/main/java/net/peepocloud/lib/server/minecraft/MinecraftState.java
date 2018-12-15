package net.peepocloud.lib.server.minecraft;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MinecraftState {

    LOBBY("Lobby"),
    IN_GAME("Ingame"),
    OFFLINE("Offline");

    private String name;
}
