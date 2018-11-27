package net.nevercloud.node.screen;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.network.packet.out.screen.NetworkScreen;
import net.nevercloud.node.network.packet.out.screen.PacketOutToggleScreen;
import net.nevercloud.node.network.participant.NodeParticipant;
import net.nevercloud.node.screen.network.NetworkScreenManager;
import net.nevercloud.node.screen.process.ProcessScreenManager;
import net.nevercloud.node.server.process.CloudProcess;

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

    public UUID enableScreen(BungeeCordProxyInfo proxyInfo, Consumer<String> consumer) {
        return enableScreen0(consumer, proxyInfo.getComponentName(), proxyInfo.getParentComponentName());
    }

    private UUID enableScreen0(Consumer<String> consumer, String componentName, String parentComponentName) {
        NodeParticipant nodeParticipant = NeverCloudNode.getInstance().getServerNodes().get(parentComponentName);
        if (nodeParticipant == null) {
            CloudProcess process = NeverCloudNode.getInstance().getProcessManager().getProcesses().get(componentName);
            if (process == null)
                return null;
            return this.processScreenManager.loadScreen(process, consumer);
        }
        return this.networkScreenManager.enableScreen0(consumer, componentName, parentComponentName);
    }

    public UUID enableScreen(MinecraftServerInfo serverInfo, Consumer<String> consumer) {
        return enableScreen0(consumer, serverInfo.getComponentName(), serverInfo.getParentComponentName());
    }

    public boolean disableScreen(String componentName, UUID uniqueId) {
        CloudProcess process = NeverCloudNode.getInstance().getProcessManager().getProcesses().get(componentName);
        if (process == null) {
            return this.networkScreenManager.disableScreen(componentName, uniqueId);
        }
        return this.processScreenManager.disableScreen(process, uniqueId);
    }

}
