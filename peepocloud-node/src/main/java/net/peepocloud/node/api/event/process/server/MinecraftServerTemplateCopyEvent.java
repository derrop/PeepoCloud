package net.peepocloud.node.api.event.process.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.server.Template;
import net.peepocloud.node.api.event.process.ProcessEvent;
import net.peepocloud.node.server.process.CloudProcess;

import java.io.InputStream;

@Getter
/**
 * Called when a process starts up and the template will tried to be copied, in the inputStream must be a zipped directory (the template)
 */
public class MinecraftServerTemplateCopyEvent extends ProcessEvent {
    private MinecraftServerInfo serverInfo;
    @Setter
    private InputStream inputStream;

    public MinecraftServerTemplateCopyEvent(CloudProcess cloudProcess, MinecraftServerInfo serverInfo, InputStream inputStream) {
        super(cloudProcess);
        this.serverInfo = serverInfo;
        this.inputStream = inputStream;
    }

    public Template getTemplate() {
        return this.serverInfo.getTemplate();
    }
}
