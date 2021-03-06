package net.peepocloud.node.api.addon.node;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import net.peepocloud.lib.network.packet.PacketManager;
import net.peepocloud.node.api.PeepoCloudNodeAPI;
import net.peepocloud.node.api.addon.Addon;
import net.peepocloud.node.api.installableplugins.InstallablePlugin;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;

public class NodeAddon extends Addon {
    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    public void initPacketHandlers(PacketManager packetManager) {}

    public final InstallablePlugin loadPluginFromJarAndSendToAllNodes(String path) {
        String name = Paths.get(path).getFileName().toString();
        InstallablePlugin plugin = new InstallablePlugin(name, "memory");
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            PeepoCloudNodeAPI.getInstance().getPluginLoader().updatePluginToAllNodes(plugin, byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return plugin;
    }

    public final InstallablePlugin loadPluginFromJar(String path) {
        String name = Paths.get(path).getFileName().toString();
        InstallablePlugin plugin = new InstallablePlugin(name, "memory");
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
            PeepoCloudNodeAPI.getInstance().getPluginLoader().updatePlugin(plugin, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return plugin;
    }

}
