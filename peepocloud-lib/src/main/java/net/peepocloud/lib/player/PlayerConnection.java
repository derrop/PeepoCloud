package net.peepocloud.lib.player;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PlayerConnection {
    private String ip;
    private int port;
    private int protocolVersion;
}
