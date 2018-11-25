package net.nevercloud.node.server.process;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.lib.server.GroupMode;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.lib.utility.ZipUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.api.event.server.BungeeCordTemplateCopyEvent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Getter
@ToString
public class BungeeProcess implements CloudProcess {
    private Path directory;
    private Process process;
    private BungeeCordProxyInfo proxyInfo;

    public BungeeProcess(BungeeCordProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
        this.directory = proxyInfo.getGroup().getGroupMode() == GroupMode.SAVE ?
                Paths.get("internal/savedProxies/" + proxyInfo.getGroupName() + "/" + proxyInfo.getComponentName()) :
                Paths.get("internal/deletingProxies/" + proxyInfo.getGroupName() + "/" + proxyInfo.getComponentName() + "#" + UUID.randomUUID());
    }

    @Override
    public int getMemory() {
        return this.proxyInfo.getMemory();
    }

    @Override
    public void startup() {

    }

    private void loadTemplate() {
        Path dir = Paths.get("templates/" + this.proxyInfo.getGroupName() + "/" + this.proxyInfo.getTemplate().getName());
        BungeeCordTemplateCopyEvent copyEvent = new BungeeCordTemplateCopyEvent(this.proxyInfo, null);
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
