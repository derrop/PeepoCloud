package net.peepocloud.node.addon;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.peepocloud.lib.config.Configurable;
import net.peepocloud.lib.config.yaml.YamlConfigurable;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;

import java.nio.file.Path;

@Getter
public abstract class Addon {

    private AddonLoader addonLoader;
    private AddonConfig addonConfig;
    private Path configFile;
    boolean enabled = false;

    public void setAddonConfig(AddonConfig addonConfig) {
        Preconditions.checkArgument(this.addonConfig == null);
        this.addonConfig = addonConfig;
    }

    public void setAddonLoader(AddonLoader addonLoader) {
        Preconditions.checkArgument(this.addonLoader == null);
        this.addonLoader = addonLoader;
    }

    public void setConfigFile(Path configFile) {
        Preconditions.checkArgument(this.configFile == null);
        this.configFile = configFile;
    }

    public void createConfig(YamlConfigurable configurable) {
        SystemUtils.createFile(this.configFile);
        configurable.saveAsFile(this.configFile);
    }

    public Configurable loadConfig() {
        return YamlConfigurable.load(this.configFile);
    }

    protected abstract void onLoad();

    protected abstract void onEnable();

    protected abstract void onDisable();

    public PeepoCloudNode getNode() {
        return PeepoCloudNode.getInstance();
    }
}
