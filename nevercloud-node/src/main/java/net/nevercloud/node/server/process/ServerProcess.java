package net.nevercloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.GroupMode;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.lib.utility.ZipUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.events.server.MinecraftServerTemplateCopyEvent;
import net.nevercloud.node.server.ServerFilesLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Getter
@ToString
public class ServerProcess implements CloudProcess {

    private Process process;
    private Path directory;
    private MinecraftServerInfo serverInfo;

    public ServerProcess(MinecraftServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        this.directory = serverInfo.getGroup().getGroupMode() == GroupMode.SAVE ?
                Paths.get("internal/savedServers/" + serverInfo.getGroupName() + "/" + serverInfo.getComponentName()) :
                Paths.get("internal/deletingServers/" + serverInfo.getGroupName() + "/" + serverInfo.getComponentName() + "#" + UUID.randomUUID());
    }

    @Override
    public int getMemory() {
        return this.serverInfo.getMemory();
    }

    @Override
    public void startup() {
        this.loadTemplate();
        this.loadSpigot();

    }

    private void loadSpigot() {
        Path path = Paths.get(this.directory.toString(), "server.jar");
        ServerFilesLoader.copySpigot(this.serverInfo, path);
    }

    private void loadTemplate() {
        Path dir = Paths.get("templates/" + this.serverInfo.getGroupName() + "/" + this.serverInfo.getTemplate().getName());
        MinecraftServerTemplateCopyEvent copyEvent = new MinecraftServerTemplateCopyEvent(this.serverInfo, null);
        NeverCloudNode.getInstance().getEventManager().callEvent(copyEvent);
        if (copyEvent.getInputStream() != null) {
            ZipUtils.unzipDirectory(copyEvent.getInputStream(), this.directory.toString());
        } else {
            SystemUtils.copyDirectory(dir, this.directory.toString());
        }
    }

    @Override
    public void shutdown() {

    }

}
