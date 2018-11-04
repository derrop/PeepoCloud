package net.nevercloud.node;
/*
 * Created by Mc_Ruben on 04.11.2018
 */

import lombok.*;

@Getter
public class NeverCloudNode {

    @Getter
    private static NeverCloudNode instance;

    NeverCloudNode() {
    }
}
