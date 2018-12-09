package net.peepocloud.node.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;

@Data
@AllArgsConstructor
public class ServerVersion {
    private String version;
    private String url;

    @Override
    public String toString() {
        return version;
    }
}
