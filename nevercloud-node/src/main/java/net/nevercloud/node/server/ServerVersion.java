package net.nevercloud.node.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;

@Data
@AllArgsConstructor
public class ServerVersion {
    private String name;
    private String url;

    @Override
    public String toString() {
        return name;
    }
}
