package net.peepocloud.node.api.addon;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import java.net.URLClassLoader;

public abstract class AddonLoader {

    public abstract URLClassLoader getClassLoader();

    public abstract Addon loadAddon(AddonConfig config);
}
