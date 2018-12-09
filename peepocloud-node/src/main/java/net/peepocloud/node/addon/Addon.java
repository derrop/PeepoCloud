package net.peepocloud.node.addon;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.peepocloud.node.PeepoCloudNode;

@Getter
public abstract class Addon {

    private AddonLoader addonLoader;
    private AddonConfig addonConfig;
    boolean enabled = false;

    public void setAddonConfig(AddonConfig addonConfig) {
        Preconditions.checkArgument(this.addonConfig == null);
        this.addonConfig = addonConfig;
    }

    public void setAddonLoader(AddonLoader addonLoader) {
        Preconditions.checkArgument(this.addonLoader == null);
        this.addonLoader = addonLoader;
    }

    protected abstract void onLoad();

    protected abstract void onEnable();

    protected abstract void onDisable();

    public PeepoCloudNode getNode() {
        return PeepoCloudNode.getInstance();
    }
}
