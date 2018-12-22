package net.peepocloud.node.api.addon;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AddonManager<Addon extends net.peepocloud.node.api.addon.Addon> {

    public abstract void loadAddons(String directory) throws IOException;

    public abstract void loadAddons(Path directory) throws IOException;

    public abstract void loadAddons(String directory, Consumer<Addon> preLoadAddon) throws IOException;

    public abstract Collection<Addon> loadAddons(Path directory, Consumer<Addon> preLoadAddon) throws IOException;

    public abstract Addon loadAddon(Path path, Consumer<Addon> preLoadAddon) throws MalformedURLException;

    public abstract boolean loadAndEnableAddon(Path path);

    public abstract void enableAddons();

    public abstract void disableAndUnloadAddons();

    public abstract void disableAddon(Addon addon);

    public abstract void unloadAddon(Addon addon);

    public abstract void enableAddon(Addon addon);

    public abstract Addon getAddonByName(String name);

    public abstract Addon getAddonByFileName(String fileName);

    public abstract void shutdown();

    public abstract Map<String, Addon> getLoadedAddons();

}
