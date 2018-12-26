package net.peepocloud.node.api.event.process.server;
/*
 * Created by Mc_Ruben on 26.12.2018
 */

import lombok.Getter;
import lombok.Setter;
import net.peepocloud.node.api.event.process.ProcessEvent;
import net.peepocloud.node.api.server.CloudProcess;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * This event is called every time a server starts and the PeepoCloud Plugin is copied to its "plugins" folder.
 * The {@link InputStream} is the stream from which the plugin will be copied.
 * The {@link Path} is the output where the plugin is copied to.
 */
@Getter
@Setter
public class MinecraftServerPluginCopyEvent extends ProcessEvent {

    private InputStream inputStream;
    private Path target;

    public MinecraftServerPluginCopyEvent(CloudProcess cloudProcess, InputStream inputStream, Path target) {
        super(cloudProcess);
        this.inputStream = inputStream;
        this.target = target;
    }
}
