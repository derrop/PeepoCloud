package net.nevercloud.node.setup.type;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

import net.nevercloud.node.setup.SetupAcceptable;

public interface BooleanSetupAcceptable extends SetupAcceptable {
    boolean onPrint(boolean input);
}
