package net.peepocloud.node.api.event.process.server;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.node.api.event.Cancellable;
import net.peepocloud.node.api.event.process.ProcessEvent;
import net.peepocloud.node.api.server.CloudProcess;

@Getter
public class ServerQueuedEvent extends ProcessEvent implements Cancellable {
    @Setter
    private boolean cancelled;
    private MinecraftServerInfo serverInfo;

    public ServerQueuedEvent(CloudProcess process, MinecraftServerInfo serverInfo) {
        super(process);
        this.serverInfo = serverInfo;
    }
}
