package net.nevercloud.node.api.event.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.nevercloud.lib.server.BungeeCordProxyInfo;
import net.nevercloud.node.api.event.internal.Event;

import java.io.InputStream;

@Getter
@AllArgsConstructor
public class BungeeCordStartupFileCopyEvent extends Event {
    private BungeeCordProxyInfo proxyInfo;
    @Setter
    private InputStream inputStream;
}
