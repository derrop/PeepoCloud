package net.peepocloud.node.installableplugins;
/*
 * Created by Mc_Ruben on 16.01.2019
 */

import lombok.AllArgsConstructor;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.installableplugins.InstallablePlugin;
import net.peepocloud.node.api.installableplugins.InstallablePluginLoader;
import net.peepocloud.node.api.network.ClientNode;
import net.peepocloud.node.api.server.TemplateStorage;
import net.peepocloud.node.network.packet.out.installableplugins.PacketOutRemoveInstallablePluginInCache;
import net.peepocloud.node.network.packet.out.installableplugins.PacketOutUpdateInstallablePluginInCache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class InstallablePluginLoaderImpl implements InstallablePluginLoader {

    private Collection<InstallingMemoryPlugin> installingMemoryPlugins = new ArrayList<>();

    public boolean loadPlugin(InstallablePlugin plugin, Path target) {
        if (Objects.equals(plugin.getBackend(), "memory")) {
            Path path = this.getPath(plugin);
            if (!Files.exists(path)) {
                return false;
            }
            SystemUtils.createParent(target);
            try {
                Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        TemplateStorage storage = PeepoCloudNode.getInstance().getTemplateStorage(plugin.getBackend());
        if (storage == null)
            return false;
        storage.copyInstallablePlugin(plugin, target);
        return true;
    }

    public void updatePlugin(InstallablePlugin plugin, InputStream inputStream) {
        Path path = this.getPath(plugin);
        if (!Files.exists(path)) {
            SystemUtils.createParent(path);
        }
        try {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePluginToNodes(Collection<ClientNode> nodes, InstallablePlugin plugin, byte[] bytes) {
        Packet packet = new PacketOutUpdateInstallablePluginInCache(plugin, bytes);
        for (ClientNode node : nodes) {
            node.sendPacket(packet);
        }
    }

    public void updatePluginToAllNodes(InstallablePlugin plugin, byte[] bytes) {
        this.updatePlugin(plugin, new ByteArrayInputStream(bytes));
        Packet packet = new PacketOutUpdateInstallablePluginInCache(plugin, bytes);
        for (ClientNode node : PeepoCloudNode.getInstance().getConnectedNodes().values()) {
            node.sendPacket(packet);
        }
        this.installingMemoryPlugins.add(new InstallingMemoryPlugin(plugin, bytes));
    }

    public void unloadPlugin(String name) {
        this.installingMemoryPlugins.removeAll(this.installingMemoryPlugins.stream().filter(installingMemoryPlugin -> installingMemoryPlugin.plugin.getName().equals(name)).collect(Collectors.toList()));
        try {
            Files.deleteIfExists(this.getPath(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unloadPluginOnAllNodes(String name) {
        this.unloadPlugin(name);
        for (ClientNode value : PeepoCloudNode.getInstance().getConnectedNodes().values()) {
            value.sendPacket(new PacketOutRemoveInstallablePluginInCache(name));
        }
    }

    public void handleNodeConnect(ClientNode node) {
        if (!this.installingMemoryPlugins.isEmpty()) {
            for (InstallingMemoryPlugin installingMemoryPlugin : this.installingMemoryPlugins) {
                node.sendPacket(new PacketOutUpdateInstallablePluginInCache(installingMemoryPlugin.plugin, installingMemoryPlugin.bytes));
            }
        }
    }

    private Path getPath(InstallablePlugin plugin) {
        return this.getPath(plugin.getName());
    }

    private Path getPath(String name) {
        return Paths.get("internal/cache/memoryPlugins/" + name);
    }

    @AllArgsConstructor
    private static final class InstallingMemoryPlugin {
        private InstallablePlugin plugin;
        private byte[] bytes;
    }

}
