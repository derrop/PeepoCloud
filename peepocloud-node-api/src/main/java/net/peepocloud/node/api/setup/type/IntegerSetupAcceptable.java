package net.peepocloud.node.api.setup.type;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

import net.peepocloud.node.api.setup.SetupAcceptable;

public interface IntegerSetupAcceptable extends SetupAcceptable {
    boolean onPrint(int input);
}
