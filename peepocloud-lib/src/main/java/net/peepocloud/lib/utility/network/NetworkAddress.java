package net.peepocloud.lib.utility.network;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NetworkAddress {
    private String host;
    private int port;

    @Override
    public String toString() {
        return this.host + ":" + this.port;
    }
}
