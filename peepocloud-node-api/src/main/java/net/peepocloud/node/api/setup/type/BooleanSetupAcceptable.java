package net.peepocloud.node.api.setup.type;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

import net.peepocloud.node.api.setup.SetupAcceptable;

public interface BooleanSetupAcceptable extends SetupAcceptable {
    boolean onPrint(boolean input);
}
