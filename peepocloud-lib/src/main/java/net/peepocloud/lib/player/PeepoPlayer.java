package net.peepocloud.lib.player;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PeepoPlayer {

    private UUID uniqueId;
    private String name;
    private PlayerConnection playerConnection;
    private String proxyName;
    @Setter
    private String serverName;

    public void sendMessage(String message) {
    }

    public void connect(String server) {
    }

    public void kick(String reason) {
    }

    public void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
    }

    public void sendActionBar(String message) {
    }

}
