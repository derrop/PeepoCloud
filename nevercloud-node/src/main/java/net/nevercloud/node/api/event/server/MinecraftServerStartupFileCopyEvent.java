package net.nevercloud.node.api.event.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.api.event.internal.Event;
import net.nevercloud.node.server.process.CloudProcess;

import java.io.InputStream;

@Getter
public class MinecraftServerStartupFileCopyEvent extends ProcessEvent {
    private MinecraftServerInfo serverInfo;
    @Setter
    private InputStream inputStream;

    public MinecraftServerStartupFileCopyEvent(CloudProcess cloudProcess, MinecraftServerInfo serverInfo, InputStream inputStream) {
        super(cloudProcess);
        this.serverInfo = serverInfo;
        this.inputStream = inputStream;
    }
}
