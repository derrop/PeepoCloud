package net.nevercloud.node.screen.network;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import lombok.*;
import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.network.packet.out.screen.NetworkScreen;
import net.nevercloud.node.network.packet.out.screen.PacketOutToggleScreen;
import net.nevercloud.node.network.participant.NodeParticipant;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class NetworkScreenManager {

    private Map<String, NetworkScreen> screens = new HashMap<>();

    public UUID enableScreen(BungeeCordProxyInfo proxyInfo, Consumer<String> consumer) {
        return enableScreen0(consumer, proxyInfo.getComponentName(), proxyInfo.getParentComponentName());
    }

    public UUID enableScreen0(Consumer<String> consumer, String componentName, String parentComponentName) {
        UUID uniqueId = UUID.randomUUID();
        if (this.screens.containsKey(componentName)) {
            this.screens.get(componentName).getConsumers().put(uniqueId, consumer);
            return uniqueId;
        }
        NodeParticipant nodeParticipant = NeverCloudNode.getInstance().getServerNodes().get(parentComponentName);
        if (nodeParticipant == null)
            return null;
        nodeParticipant.sendPacket(new PacketOutToggleScreen(componentName, true));
        this.screens.put(componentName, new NetworkScreen(nodeParticipant));
        this.screens.get(componentName).getConsumers().put(uniqueId, consumer);
        return uniqueId;
    }

    public UUID enableScreen(MinecraftServerInfo serverInfo, Consumer<String> consumer) {
        return enableScreen0(consumer, serverInfo.getComponentName(), serverInfo.getParentComponentName());
    }

    public boolean disableScreen(String componentName, UUID uniqueId) {
        NetworkScreen screen = this.screens.get(componentName);
        if (screen == null)
            return false;
        if (!screen.getConsumers().containsKey(uniqueId))
            return false;
        screen.getConsumers().remove(uniqueId);
        if (screen.getConsumers().isEmpty()) {
            screen.getParticipant().sendPacket(new PacketOutToggleScreen(componentName, false));
            this.screens.remove(componentName);
        }
        return true;
    }

    public void handleNodeDisconnect(NodeParticipant participant) {
        new HashMap<>(this.screens).forEach((s, networkScreen) -> { //prevent ConcurrentModificationException
            if (networkScreen.getParticipant().equals(participant)) {
                this.screens.remove(s);
            }
        });
    }

    public void dispatchScreenInput(NodeParticipant participant, String name, String line) {
        if (line == null)
            return;
        if (!this.screens.containsKey(name)) {
            participant.sendPacket(new PacketOutToggleScreen(name, false));
            return;
        }

        this.screens.get(name).call(line);
    }

}
