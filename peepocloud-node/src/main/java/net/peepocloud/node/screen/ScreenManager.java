package net.peepocloud.node.screen;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import lombok.*;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.api.server.minecraft.MinecraftServerInfo;
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

    /**
     * Enables a screen for the given {@link BungeeCordProxyInfo} and posts the complete log live to the given {@link Consumer}
     * @param proxyInfo the proxyInfo from which we get the screen
     * @param consumer the consumer to which we post the log
     * @return the enabled screen containing the uniqueId of the screen and the name of the proxy to disable it
     */
    public EnabledScreen enableScreen(BungeeCordProxyInfo proxyInfo, Consumer<String> consumer) {
        return enableScreen0(consumer, proxyInfo.getComponentName(), proxyInfo.getParentComponentName());
    }

    /**
     * Enables a screen for the given {@link MinecraftServerInfo} and posts the complete log live to the given {@link Consumer}
     * @param serverInfo the serverInfo from which we get the screen
     * @param consumer the consumer to which we post the log
     * @return the enabled screen containing the uniqueId of the screen and the name of the server to disable it
     */
    public EnabledScreen enableScreen(MinecraftServerInfo serverInfo, Consumer<String> consumer) {
        return enableScreen0(consumer, serverInfo.getComponentName(), serverInfo.getParentComponentName());
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

    /**
     * Disables a screen if its running
     * @param componentName the name of the component from which the screen should be disabled
     * @param uniqueId the uniqueId of the screen that should be disabled
     * @return {@code true} if it has been disabled successfully or {@code false} if it was not enabled
     */
    public boolean disableScreen(String componentName, UUID uniqueId) {
        CloudProcess process = PeepoCloudNode.getInstance().getProcessManager().getProcesses().get(componentName);
        if (process == null) {
            return this.networkScreenManager.disableScreen(componentName, uniqueId);
        }
        return this.processScreenManager.disableScreen(process, uniqueId);
    }

    /**
     * Disables a screen if its running
     * @param enabledScreen the screen to disable
     * @return {@code true} if it has been disabled successfully or {@code false} if it was not enabled
     */
    public boolean disableScreen(EnabledScreen enabledScreen) {
        return this.disableScreen(enabledScreen.getComponentName(), enabledScreen.getUniqueId());
    }

    /**
     * @deprecated for internal use only
     * @param process the process which was stopped
     */
    @Deprecated
    public void handleProcessStop(CloudProcess process) {
        if (this.networkScreenManager.getScreens().containsKey(process.getName())) {
            this.networkScreenManager.disableScreen(process.getName());
        }
    }

}
