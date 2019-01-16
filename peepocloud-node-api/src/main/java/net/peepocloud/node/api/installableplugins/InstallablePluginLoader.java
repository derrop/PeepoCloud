package net.peepocloud.node.api.installableplugins;
/*
 * Created by Mc_Ruben on 16.01.2019
 */

import net.peepocloud.node.api.network.ClientNode;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;

public interface InstallablePluginLoader {

    /**
     * Loads the given {@link InstallablePlugin} from its backend (if it is not "memory") to the given {@link Path}
     *
     * @param plugin the plugin to load
     * @param target the path where the plugins jar should be copied to
     * @return if it has been copied successful
     */
    boolean loadPlugin(InstallablePlugin plugin, Path target);

    /**
     * Removes the given plugin from the cache on all to this Node connected Nodes
     *
     * @param name the name of the plugin
     */
    void unloadPluginOnAllNodes(String name);

    /**
     * Removes the given plugin from the cache in this Node
     *
     * @param name the name of the plugin
     */
    void unloadPlugin(String name);

    /**
     * Updates the given {@link InstallablePlugin} to the cache of this Node from the given {@link InputStream}
     *
     * @param plugin      the plugin to update
     * @param inputStream the stream with all bytes of the new plugin jar
     */
    void updatePlugin(InstallablePlugin plugin, InputStream inputStream);

    /**
     * Updates the given {@link InstallablePlugin} with the new given {@code bytes} to all given Nodes
     *
     * @param nodes  the nodes where the plugin should be updated
     * @param plugin the plugin to update
     * @param bytes  the bytes of the new plugin jar
     * @see InstallablePluginLoader#updatePlugin(InstallablePlugin, InputStream)
     */
    void updatePluginToNodes(Collection<ClientNode> nodes, InstallablePlugin plugin, byte[] bytes);

    /**
     * Updates the given {@link InstallablePlugin} with the new given {@code bytes} to all nodes connected to this Node
     *
     * @param plugin the plugin to update
     * @param bytes  the bytes of the new plugin jar
     * @see InstallablePluginLoader#updatePlugin(InstallablePlugin, InputStream)
     * @see InstallablePluginLoader#updatePluginToNodes(Collection, InstallablePlugin, byte[])
     */
    void updatePluginToAllNodes(InstallablePlugin plugin, byte[] bytes);

}
