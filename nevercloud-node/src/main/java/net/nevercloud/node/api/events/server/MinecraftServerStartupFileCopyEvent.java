package net.nevercloud.node.api.events.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.MinecraftServerInfo;
import net.nevercloud.node.api.events.internal.Event;

import java.io.InputStream;

@Getter
@AllArgsConstructor
public class MinecraftServerStartupFileCopyEvent extends Event {
    private MinecraftServerInfo serverInfo;
    @Setter
    private InputStream inputStream;
}
