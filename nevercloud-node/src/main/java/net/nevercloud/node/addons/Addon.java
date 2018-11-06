package net.nevercloud.node.addons;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.nevercloud.node.NeverCloudNode;

@RequiredArgsConstructor
@Getter
public abstract class Addon {

    public Addon() {
        this(null, null);
    }

    private final AddonLoader addonLoader;
    private final AddonConfig addonConfig;
    boolean enabled = false;

    protected abstract void onLoad();

    protected abstract void onEnable();

    protected abstract void onDisable();

    public NeverCloudNode getNode() {
        return NeverCloudNode.getInstance();
    }
}
