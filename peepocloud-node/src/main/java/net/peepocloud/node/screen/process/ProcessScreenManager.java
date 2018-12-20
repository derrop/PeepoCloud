package net.peepocloud.node.screen.process;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.peepocloud.lib.network.NetworkPacketSender;
import net.peepocloud.node.network.packet.out.screen.PacketOutScreenLine;
import net.peepocloud.node.screen.EnabledScreen;
import net.peepocloud.node.server.process.CloudProcess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
public class ProcessScreenManager {

    private Map<CloudProcess, Collection<NetworkPacketSender>> networkScreens = new ConcurrentHashMap<>();

    public void enableNetworkScreen(CloudProcess process, NetworkPacketSender node) {
        if (!this.networkScreens.containsKey(process))
            this.networkScreens.put(process, new ArrayList<>());
        this.networkScreens.get(process).add(node);
        Collection<NetworkPacketSender> nodes = this.networkScreens.get(process);
        process.getCachedLog().forEach(s -> node.sendPacket(new PacketOutScreenLine(process.getName(), s)));
        if (process.getNetworkScreenHandler() == null) {
            process.setNetworkScreenHandler(s -> nodes.forEach(networkParticipant -> networkParticipant.sendPacket(new PacketOutScreenLine(process.getName(), s))));
        }
    }

    public void disableNetworkScreen(CloudProcess process, NetworkPacketSender node) {
        if (!this.networkScreens.containsKey(process)) {
            process.setNetworkScreenHandler(null);
            return;
        }
        this.networkScreens.get(process).remove(node);
        if (this.networkScreens.get(process).isEmpty()) {
            this.networkScreens.remove(process);
            process.setNetworkScreenHandler(null);
        }
    }

    public EnabledScreen loadScreen(CloudProcess process, Consumer<String> consumer) {
        Preconditions.checkArgument(process.isRunning(), "process must be active to start a screen");
        UUID uniqueId = UUID.randomUUID();
        new ArrayList<>(process.getCachedLog()).forEach(consumer);
        process.getScreenHandlers().put(uniqueId, consumer);
        return new EnabledScreen(process.getName(), uniqueId) {
            @Override
            public void write(String line) {
                process.dispatchCommand(line);
            }
        };
    }

    public boolean disableScreen(CloudProcess process, UUID uniqueId) {
        if (!process.getScreenHandlers().containsKey(uniqueId))
            return false;
        process.getScreenHandlers().remove(uniqueId);
        return true;
    }

    public boolean disableScreen(CloudProcess process) {
        process.getScreenHandlers().clear();
        return true;
    }

}
