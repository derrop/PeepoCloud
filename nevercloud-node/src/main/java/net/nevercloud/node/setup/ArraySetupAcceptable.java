package net.nevercloud.node.setup;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;

@AllArgsConstructor
public class ArraySetupAcceptable<T> implements StringSetupAcceptable {
    private T[] values;
    @Override
    public boolean onPrint(String input) {
        for (T value : values) {
            if (value != null && value.toString().equalsIgnoreCase(input))
                return true;
        }
        return false;
    }
}
