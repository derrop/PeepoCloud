package net.peepocloud.lib.server.minecraft;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public enum MinecraftState implements Serializable {

    LOBBY("Lobby"),
    IN_GAME("Ingame"),
    OFFLINE("Offline");

    private String name;
}
