package net.nevercloud.node.setup;
/*
 * Created by Mc_Ruben on 12.11.2018
 */

public interface BooleanSetupAcceptable extends SetupAcceptable {
    boolean onPrint(boolean input);
}