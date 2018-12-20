package net.peepocloud.lib.node;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
