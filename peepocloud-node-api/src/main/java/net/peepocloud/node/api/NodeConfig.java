package net.peepocloud.node.api;
/*
 * Created by Mc_Ruben on 01.01.2019
 */

import net.peepocloud.lib.utility.network.NetworkAddress;

public abstract class NodeConfig {

    public abstract String getNodeName();

    public abstract NetworkAddress getHost();

    public abstract int getMaxMemory();

    public abstract boolean isAutoUpdate();

    public abstract String getBungeeStartCmd();

    public abstract String getServerStartCmd();

    public abstract String getUsername();

    public abstract String getUniqueId();

    public abstract String getApiToken();

    public abstract boolean isUseGlobalStats();

}
