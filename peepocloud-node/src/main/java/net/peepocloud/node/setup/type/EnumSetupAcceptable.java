package net.peepocloud.node.setup.type;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EnumSetupAcceptable implements StringSetupAcceptable {
    private Class<? extends Enum> anEnum;
    @Override
    public boolean onPrint(String input) {
        for (Enum enumConstant : anEnum.getEnumConstants()) {
            if (enumConstant.name().equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }
}
