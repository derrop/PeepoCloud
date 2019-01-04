package net.peepocloud.lib.player;
/*
 * Created by Mc_Ruben on 04.01.2019
 */

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
public class PeepoPlayerSkinConfiguration {

    private boolean cape, jacket, leftSleeve, rightSleeve, leftPants, rightPants, hat;

    public byte toByte() {
        boolean[] bo = new boolean[]{cape, jacket, leftSleeve, rightSleeve, leftPants, rightPants, hat};
        byte by = 0;
        int i = 0;
        for (boolean b : bo) {
            by += b ? 1 << i++ : 0;
        }
        return by;
    }

    public static PeepoPlayerSkinConfiguration fromByte(byte bitmask) {
        return new PeepoPlayerSkinConfiguration(
                ((bitmask) & 1) == 1,
                ((bitmask >> 1) & 1) == 1,
                ((bitmask >> 2) & 1) == 1,
                ((bitmask >> 3) & 1) == 1,
                ((bitmask >> 4) & 1) == 1,
                ((bitmask >> 5) & 1) == 1,
                ((bitmask >> 6) & 1) == 1
        );
    }

}
