package net.peepocloud.lib.node;
/*
 * Created by Mc_Ruben on 20.12.2018
 */

import lombok.*;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class NodeStartupPriority {
    private String node;
    private int priority;
}
