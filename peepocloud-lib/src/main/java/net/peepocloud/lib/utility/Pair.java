package net.peepocloud.lib.utility;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import lombok.*;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Pair<K, V> {

    private K key;
    private V value;

}
