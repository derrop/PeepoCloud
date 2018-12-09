package net.peepocloud.node.screen;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import lombok.*;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.network.participant.NodeParticipant;
import net.peepocloud.node.screen.network.NetworkScreenManager;
import net.peepocloud.node.screen.process.ProcessScreenManager;
import net.peepocloud.node.server.process.CloudProcess;

import java.util.UUID;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public class ScreenManager {

    public ScreenManager() {
        this.processScreenManager = new ProcessScreenManager();
        this.networkScreenManager = new NetworkScreenManager();
    }

    private ProcessScreenManager processScreenManager;
    private NetworkScreenManager networkScreenManager;

    public EnabledScreen enableScreen(BungeeCordProxyInfo proxyInfo, Consumer<String> consumer) {
        return enableScreen0(consumer, proxyInfo.getComponentName(), proxyInfo.getParentComponentName());
    }

    private EnabledScreen enableScreen0(Consumer<String> consumer, String componentName, String parentComponentName) {
        NodeParticipant nodeParticipant = PeepoCloudNode.getInstance().getServerNodes().get(parentComponentName);
        if (nodeParticipant == null) {
            CloudProcess process = PeepoCloudNode.getInstance().getProcessManager().getProcesses().get(componentName);
            if (process == null)
                return null;
            return this.processScreenManager.loadScreen(process, consumer);
        }
        return this.networkScreenManager.enableScreen(consumer, componentName, parentComponentName);
    }

    public EnabledScreen enableScreen(MinecraftServerInfo serverInfo, Consumer<String> consumer) {
        return enableScreen0(consumer, serverInfo.getComponentName(), serverInfo.getParentComponentName());
    }

    public boolean disableScreen(String componentName, UUID uniqueId) {
        CloudProcess process = PeepoCloudNode.getInstance().getProcessManager().getProcesses().get(componentName);
        if (process == null) {
            return this.networkScreenManager.disableScreen(componentName, uniqueId);
        }
        return this.processScreenManager.disableScreen(process, uniqueId);
    }

    public boolean disableScreen(EnabledScreen enabledScreen) {
        return this.disableScreen(enabledScreen.getComponentName(), enabledScreen.getUniqueId());
    }

}
