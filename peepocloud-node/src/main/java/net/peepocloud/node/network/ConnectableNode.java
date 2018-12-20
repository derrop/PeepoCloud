package net.peepocloud.node.network;
/*
 * Created by Mc_Ruben on 12.12.2018
 */

import lombok.*;
import net.peepocloud.lib.utility.network.NetworkAddress;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ConnectableNode {
    private String name;
    private NetworkAddress address;
}
