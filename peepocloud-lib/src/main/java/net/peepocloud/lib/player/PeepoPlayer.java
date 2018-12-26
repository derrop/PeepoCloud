package net.peepocloud.lib.player;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import lombok.*;
import net.peepocloud.lib.AbstractPeepoCloudAPI;

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
        AbstractPeepoCloudAPI.getInstance().sendPlayerMessage(this.uniqueId, message);
    }

    public void connect(String server) {
        AbstractPeepoCloudAPI.getInstance().sendPlayer(this.uniqueId, server);
    }

    public void kick(String reason) {
        AbstractPeepoCloudAPI.getInstance().kickPlayer(this.uniqueId, reason);
    }

    public void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        AbstractPeepoCloudAPI.getInstance().sendPlayerTitle(this.uniqueId, title, subTitle, fadeIn, stay, fadeOut);
    }

    public void sendActionBar(String message) {
        AbstractPeepoCloudAPI.getInstance().sendPlayerActionBar(this.uniqueId, message);
    }

}
