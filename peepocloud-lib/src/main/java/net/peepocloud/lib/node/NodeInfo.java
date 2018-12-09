package net.peepocloud.lib.node;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import lombok.*;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class NodeInfo {
    private String name;
    private int maxMemory;
    private int usedMemory;
    private double cpuUsage;
}
