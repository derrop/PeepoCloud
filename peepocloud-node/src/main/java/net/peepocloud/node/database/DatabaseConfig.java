package net.peepocloud.node.database;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import lombok.*;

@Data
@AllArgsConstructor
public class DatabaseConfig {

    private String host;
    private int port;
    private String username;
    private String password;
    private String database;

}
